package org.dbpedia.ld.fetch

import io.netty.channel.ConnectTimeoutException
import org.dbpedia.ldr.core.SimplifiedException
import org.dbpedia.ldr.core.metadata.old.{FetchResultDoc, IriDoc}
import org.dbpedia.ldr.core.pinguin.http.{HttpClientConfig, HttpClientJava, HttpExchangeResult}
import org.dbpedia.ldr.core.util.GZUtil
import org.slf4j.LoggerFactory

import java.io.{Closeable, IOException}
import java.net.http.{HttpConnectTimeoutException, HttpTimeoutException}
import java.util
import java.util.concurrent.{ConcurrentLinkedQueue, Executor, Executors, TimeUnit, TimeoutException}
import scala.collection.JavaConverters.{collectionAsScalaIterableConverter, seqAsJavaListConverter}
import scala.collection.mutable.ListBuffer

class FetchThread(conf: FetchConfig, services: FetchServices) extends Runnable with Closeable {

  private val log = LoggerFactory.getLogger(classOf[FetchThreadAlt])

  private val clientConf = new HttpClientConfig(
    hostFilter = services.hostFilter,
    minRequestDelay = conf.minRetryAfter.toLong,
    maxRequestDelay = conf.maxRetryAfter.toLong,
    maxResourceSize = conf.maxResourceSize.toLong,
  )
  private val client = new HttpClientJava(clientConf)

  private val balancer = services.workBalancer
  private val iriCollection = services.iriCollection
  private val hostFilter = services.hostFilter

  private var continue = true

  override def run(): Unit = {
    while (continue) {
      processHost()
    }
  }

  private def processHost(): Unit = {
    var host: Option[String] = None
    while (host.isEmpty) {
      host = balancer.nextHost()
      Thread.sleep(500)
    }

    val hostFilterResult = hostFilter.tryHost(host.get)

    val iriDocs =
      if (conf.seed == null) {
        iriCollection.findByHost(host.get)
      } else {
        iriCollection.findByHostAndSeed(host.get, conf.seed)
      }

    if (hostFilterResult.accepted) {
      if (conf.skipExisting) {
        val (skipped, filteredDocs) = filterExistingDocs(iriDocs)
        log.info(s"SKIPPED $skipped/${iriDocs.size()} @ $host")
        processIrisWithTimeOut(host.get, filteredDocs)
      } else if(conf.retryStatus != Integer.MIN_VALUE) {
        val (skipped, filteredDocs) = filterStatusDocs(iriDocs)
        log.info(s"SKIPPED (status != ${conf.retryStatus}) $skipped/${iriDocs.size} @ $host")
        processIrisWithTimeOut(host.get, filteredDocs)
      } else {
        processIrisWithTimeOut(host.get, iriDocs)
      }
    } else {
      skipIris(iriDocs.asScala.toIterator, hostFilterResult.error.get)
    }

    balancer.hostDone(host.get)
    host = None
  }

  private def filterStatusDocs(docs: util.List[IriDoc]): (Integer, util.List[IriDoc]) = {
    var skipped = 0
    val filteredDocs =
      docs.asScala.flatMap({
        doc =>
          val possibleFetchDoc = services.fetchResultCollection.findById(doc.location)
          if (possibleFetchDoc.isPresent && possibleFetchDoc.get().statusCode == conf.retryStatus) {
            Some(doc)
          } else {
            skipped += 1
            None
          }
      }).toList.asJava
    (skipped, filteredDocs)
  }

  private def filterExistingDocs(docs: util.List[IriDoc]): (Integer, util.List[IriDoc]) = {
    var skipped = 0
    val filteredDocs =
      docs.asScala.flatMap({
        doc =>
          val possibleFetchDoc = services.fetchResultCollection.findById(doc.location)
          if (possibleFetchDoc.isPresent) {
            skipped += 1
            None
          } else {
            Some(doc)
          }
      }).toList.asJava
    (skipped, filteredDocs)
  }

  private val executor = Executors.newSingleThreadExecutor()

  private def processIrisWithTimeOut(host: String, iriDocs: util.List[IriDoc]): Unit = {
    val queue = new ConcurrentLinkedQueue[IriDoc](iriDocs)
    val cqit = new ConsumingQueueIterator[IriDoc](queue)

    val runnable = new Runnable {
      override def run(): Unit = {
        processIris(cqit)
      }
    }
    val timeout = getTimeoutSeconds(iriDocs.size())
    val future = executor.submit(runnable)
    try {
      future.get(timeout, TimeUnit.SECONDS)
    } catch {
      case te: TimeoutException =>
        val message = s"max time ${timeout}s for ${host} reached"
        log.error(message)
        val ex = new SimplifiedException(classOf[HostTookToLongException].getName, message)
        if (cqit.current.isDefined) skipIri(cqit.current.get, ex)
        skipIris(queue.asScala.toIterator, ex)
        future.cancel(true)
      case ex: Exception =>
        val message = s"exception at $host for ${cqit.current}: ${ex.getClass.getName}: ${ex.getMessage}"
        log.error(message)
      // TODO
    }
  }

  private def getTimeoutSeconds(size: Integer): Long = {
    size * conf.avgResourceTime + conf.minHostTime
  }

  private def processIris(iriDocs: Iterator[IriDoc]): Unit = {

    val exceptionLimit = conf.hostExceptionLimit
    val exceptionFilter = List(
      classOf[IOException],
      classOf[ConnectTimeoutException].getName,
      classOf[HttpConnectTimeoutException].getName,
      classOf[HttpTimeoutException].getName
    )
    val exceptionList = new ListBuffer[String]()

    iriDocs.grouped(50).map({
      iriDocBatch =>
        iriDocBatch.map({
          iriDoc =>
            if (exceptionList.size >= exceptionLimit) {
              skipIri(
                iriDoc,
                SimplifiedException(
                  classOf[ToManyFailuresInARowException].getName,
                  s">= $exceptionLimit: ${exceptionList.mkString("|")}"
                )
              )
            } else {
              val httpExchangeResult = client.request(iriDoc.toIRI)
              // check error
              if (httpExchangeResult.error.isDefined) {
                // append exception list if relevant
                val errorClassName = httpExchangeResult.error.get.className
                if (exceptionFilter.contains(errorClassName)) {
                  exceptionList.append(errorClassName)
                } else {
                  exceptionList.clear()
                }
              } else {
                exceptionList.clear()
              }

              // wait maxRetry if last code was 429 // TODO 503
              if (httpExchangeResult.statusCode == 429) {
                val maxDelay = conf.maxRetryAfter - httpExchangeResult.duration
                if (0 < maxDelay) Thread.sleep(maxDelay)
              }

              // save body to database
              storeData(httpExchangeResult)
              // convert to FetchResultDoc
              resultConversion(httpExchangeResult)
            }
        })
    }).foreach({
      docBatch =>
        storeMetadata(docBatch.toList)
    })
  }

  def storeData(value: HttpExchangeResult): Boolean = {
    if (null != value.body) {
      services.writeBehindDataService.write(
        FetchDataRow(value.iri.sha256sum, value.start, GZUtil.toCompressedByteArray(value.body))
      )
    }
    true
  }

  def storeMetadata(docs: List[FetchResultDoc]): List[Boolean] = {
    docs.map({
      doc =>
        services.fetchResultCollection.save(doc)
        true
    })
  }

  private def skipIris(iriDocs: Iterator[IriDoc], exception: SimplifiedException): Unit = {
    iriDocs.map({
      iriDoc => skipIri(iriDoc, exception)
    }).grouped(50).foreach({
      batch =>
        services.fetchResultCollection.saveAll(batch.toList.asJava)
    })
  }

  private def skipIri(iriDoc: IriDoc, error: SimplifiedException): FetchResultDoc = {
    val doc = new FetchResultDoc()
    doc.location = (iriDoc.location)
    doc.locationHash = (iriDoc.locationHash)
    doc.host = (iriDoc.host)
    doc.seed = (iriDoc.seed)
    doc.error = (error.className)
    doc.errorMessage = (error.msg)
    doc.timeStamp = (System.currentTimeMillis())
    doc.duration = (0)
    doc
  }

  private def resultConversion(in: HttpExchangeResult): FetchResultDoc = {
    val doc = new FetchResultDoc()
    val iri = in.iri
    doc.location = (iri.toString)
    doc.locationHash = (iri.sha256sum)
    doc.host = (iri.host)
    doc.statusCode = (in.statusCode)
    if (in.bodySize.isDefined) doc.dataSize = (in.bodySize.get)
    doc.followedIri = (in.followedIRIs.map({
      followedIRI =>
        val map: util.Map[String, String] = new util.TreeMap[String, String]()
        map.put("type", followedIRI._1)
        map.put("value", followedIRI._2.toString)
        map
    }).asJava)
    doc.headers = (in.headers.asJava)
    if (in.mimeType.isDefined) doc.contentType = (in.mimeType.get)
    if (in.charSet.isDefined) doc.charset = (in.charSet.get)
    doc.seed = (util.Arrays.asList(conf.seed))
    doc.timeStamp = (in.start)
    doc.duration = (in.duration)
    if (in.error.isDefined) {
      doc.error = (in.error.get.className)
      doc.errorMessage = (in.error.get.msg)
    }
    doc
  }

  override def close(): Unit = {
    continue = false // TODO and wait?
  }
}
