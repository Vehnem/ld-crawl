//package org.dbpedia.ld.crawl.worker.tmp
//
//import org.dbpedia.ld.crawl.config.DomainWorkerConfig
//import org.dbpedia.ld.crawl.dns.DnsEntry
//import org.dbpedia.ldr.core.metadata.{FetchResult, IriDoc}
//import org.dbpedia.ldr.core.util.GZUtil
//import org.slf4j.LoggerFactory
//
//import java.io.Closeable
//import java.net.URI
//import scala.collection.JavaConverters.{collectionAsScalaIterableConverter, seqAsJavaListConverter}
//import scala.util.{Failure, Success}
//
//class DomainWorker(
//  workerConfig: DomainWorkerConfig
//) extends Runnable with Closeable {
//
//  private val log = LoggerFactory.getLogger(classOf[DomainWorker])
//
////  private val clientConfig = new LDClientConfig()
////  clientConfig.setTimeout(workerConfig.crawlerConfig.timeout)
////  clientConfig.setRedirects(workerConfig.crawlerConfig.maxRedirects)
////  clientConfig.setAcceptHeader(workerConfig.crawlerConfig.acceptHeader)
////  clientConfig.setMaxRetryAfter(workerConfig.crawlerConfig.maxRedirects)
////  clientConfig.setMinRetryAfter(workerConfig.crawlerConfig.minRetryAfter)
//
////  private val ldClient = new LDClientPingUiNImpl(clientConfig)
//
//  case class RequestedLDR(uriCandidates: Array[URI], bytes: Array[Byte], contentType: String)
//
//  var skipDomain = false
//
//  private var isClosed: Boolean = false
//
//  private var currentDomain: Option[String] = None
//
//  private var domainConnectionTimeoutCounter = 0
//
//  private val domainConnectionTimeoutLimit = 20
//
//  override def run(): Unit = {
//    while (!isClosed) {
//
//      // get next host
//      currentDomain = workerConfig.redisHostBalancer.nextHost()
//      while (currentDomain.isEmpty) {
//        Thread.sleep(100)
//        currentDomain = workerConfig.redisHostBalancer.nextHost()
//      }
//      val domain = currentDomain.get
//      domainConnectionTimeoutCounter = 0
//
//      workerConfig.dnsService.resolveAndTry(domain) match {
//        case exception if exception.errorClass != null =>
//          // TODO create entry anyway?
//          log.error("skipping domain " + domain + " " + exception.errorClass + ": " + exception.errorMessage)
//          skipUris(workerConfig.seedURIRepo.findByHostAndSeed(domain, workerConfig.seedFilter).asScala.toList, exception)
//        case _ =>
//          // TODO only unprocessed
//          log.info("processing domain " + domain)
//          processURIs(workerConfig.seedURIRepo.findByHostAndSeed(domain, workerConfig.seedFilter).asScala.toList)
//      }
//      workerConfig.redisHostBalancer.hostDone(domain)
//    }
//  }
//
//  def skipUris(uriEntries: List[IriDoc], error: DnsEntry): Unit = {
//    uriEntries.map({
//      uriEntry =>
//        val requestEntry = new FetchResult()
//        requestEntry.setUri(uriEntry.uri)
//        requestEntry.setErrorClass(error.errorMessage)
//        requestEntry.setErrorMessage(error.errorMessage)
//        requestEntry.setTimeStamp(System.currentTimeMillis())
//        requestEntry
//    }).grouped(100).foreach({
//      batch =>
//        workerConfig.requestedResourceRepo.saveAll(batch.asJava)
//    })
//  }
//
//  def processURIs(uriEntries: List[IriDoc]): Unit = {
//    var skipCount = 0
//    uriEntries.foreach({
//      uriEntry: IriDoc =>
//
//        if (domainConnectionTimeoutCounter > domainConnectionTimeoutLimit) {
//          // TODO
//          val requestEntry = new FetchResult()
//          requestEntry.setUri(uriEntry.uri.split("#").head)
//          requestEntry.setLocationHash(uriEntry.locationHash)
//          requestEntry.setErrorClass("java.net.http.HttpConnectTimeoutException")
//          requestEntry.setErrorMessage("assuming Http connect timed out")
//          requestEntry.setTimeStamp(System.currentTimeMillis())
//          workerConfig.requestedResourceRepo.save(requestEntry)
//          skipCount += 1
//          None
//        } else {
//          // TODO rebuild from postgres cache
//          wasRequestedBefore(uriEntry.uri.split("#").head) match {
//            case Some(fetchMD) =>
//              // TODO
//              skipCount += 1
//            case None =>
//              // execute linked data request
//              fetchLDR(uriEntry) match {
//                case Some(_) =>
//                // TODO
//                case None =>
//                // TODO
//              }
//          }
//        }
//    })
//    log.info(s"skipped uris $skipCount/${uriEntries.length} of domain ${currentDomain.get}")
//    workerConfig.redisHostBalancer.hostDone(currentDomain.get)
//    currentDomain = None
//  }
//
//  def wasRequestedBefore(id: String): Option[FetchResult] = {
//    val javaOptional = workerConfig.requestedResourceRepo.findById(id)
//    if (javaOptional.isPresent)
//      Some(javaOptional.get())
//    else
//      None
//  }
//
//  def fetchLDR(uriEntry: IriDoc): Option[RequestedLDR] = {
//
//    val uri = new URI(uriEntry.uri.split("#").head) // TODO save?
//
//    ldClient.resolve(uri.toString) match {
//
//      case Failure(exception) =>
//        if (exception.isInstanceOf[java.net.http.HttpConnectTimeoutException]
//          || exception.isInstanceOf[java.net.http.HttpTimeoutException]) {
//          domainConnectionTimeoutCounter += 1
//        }
//
//        val requestEntry = new FetchResult
//        requestEntry.setUri(uri.toString)
//        requestEntry.setLocationHash(uriEntry.locationHash)
//        requestEntry.setErrorClass(exception.getClass.getName)
//        requestEntry.setErrorMessage(exception.getMessage)
//        requestEntry.setTimeStamp(System.currentTimeMillis())
//        workerConfig.requestedResourceRepo.save(requestEntry)
//        None
//
//      case Success(receivedLDResponse) =>
//
//        if (domainConnectionTimeoutCounter > 0) {
//          domainConnectionTimeoutCounter -= 1
//        }
//
//        workerConfig.dataStoreService.writeBehind(
//          uriEntry.locationHash,
//          receivedLDResponse.timeStamp,
//          GZUtil.toCompressedByteArray(receivedLDResponse.bytes)
//        )
//
//        val headers = extractHeaders(receivedLDResponse)
//        val ctHeader = extractMimeType(headers)
//
//        val requestEntry = new FetchResult()
//        requestEntry.setUri(uri.toString)
//        requestEntry.setLocationHash(uriEntry.locationHash)
//        requestEntry.setRedirectsTo(java.util.Arrays.asList[String](receivedLDResponse.redirectsTo: _*))
//        requestEntry.setStatusCode(receivedLDResponse.statusCode.toString)
//        requestEntry.setContentType(ctHeader)
//        requestEntry.setHeaders(headers)
//        requestEntry.setTimeStamp(receivedLDResponse.timeStamp)
//        workerConfig.requestedResourceRepo.save(requestEntry)
//
//        // TODO
//        //        if (receivedLDResponse.statusCode == 200) {
//        //          val uriCandidates = new ListBuffer[URI]()
//        //          uriCandidates.append(uri)
//        //          uriCandidates.appendAll(receivedLDResponse.redirectsTo.map(UriNormalizer.parseURI).map(_.get))
//        //          Some(RequestedLDR(uriCandidates.toArray, receivedLDResponse.bytes, ctHeader))
//        //        } else {
//        //          None
//        //        }
//        None
//    }
//  }
//
//  def extractHeaders(receivedLDResponse: ReceivedLDResponse): java.util.Map[String, java.util.ArrayList[String]] = {
//    receivedLDResponse.headers.map(
//      x =>
//        x._1 -> new java.util.ArrayList[String](x._2.asJava)
//    ).asJava
//  }
//
//  def extractMimeType(headers: java.util.Map[String, java.util.ArrayList[String]]): String = {
//    headers.getOrDefault(
//      "content-type",
//      new java.util.ArrayList[String](List("").asJava) // TODO empty string is better
//    ).asScala.head.split(";").head
//  }
//
//  override def close(): Unit = {
//    isClosed = true
//  }
//}
