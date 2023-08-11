package org.dbpedia.ldr.core.pinguin.http

import ch.qos.logback.classic.{Level, Logger}
import org.apache.commons.io.IOUtils
import org.dbpedia.ldr.core.SimplifiedException
import org.dbpedia.ldr.core.filter.{HostNotAllowedException, NoHostFilter}
import org.dbpedia.ldr.core.iri.IRI
import org.slf4j.LoggerFactory

import java.io.{IOException, InputStream}
import java.net.URL
import java.net.http.{HttpClient, HttpRequest, HttpResponse => JavaHttpResponse}
import java.time.Duration
import java.util.Optional
import java.util.concurrent.Executors
import scala.collection.JavaConverters.{collectionAsScalaIterableConverter, mapAsScalaMapConverter}
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}
import java.util

class HttpClientJava(conf: HttpClientConfig) {

  private val log = LoggerFactory.getLogger(classOf[HttpClientJava]).asInstanceOf[Logger]

  private val httpClient: HttpClient =
    HttpClient.newBuilder()
      .followRedirects(HttpClient.Redirect.NEVER)
      .executor(Executors.newSingleThreadExecutor())
      .build()

  // TODO configurable


  def request(iri: IRI): HttpExchangeResult = {
    log.trace(s"RETRIEVE $iri")
    val startTimestamp = System.currentTimeMillis()

    var location = iri.normalize(true).locationIRI
    var httpResponse: JavaHttpResponse[InputStream] = null
    var doNextRequest = true
    var redirectCount = 0

    var statusCode: Int = -1
    val headers = new ListBuffer[util.Map[String, util.List[String]]]()
    var body: Array[Byte] = null
    val followedIRIs = new ListBuffer[(String, IRI)]()
    var error: Option[SimplifiedException] = None

    // TODO scala while loop with return
    while (doNextRequest) {
      doNextRequest = false

      get(location) match {
        case Failure(exception) =>
          log.trace(s"FAILURE HTTP GET $iri")
          error = Some(SimplifiedException(exception.getClass.getName, exception.getMessage))
        case Success(latestResponse) =>
          val responseTimestamp = System.currentTimeMillis()
          log.trace(s"SUCCESS HTTP GET $location")
          httpResponse = latestResponse

          // handle redirects RFC2616 14.37
          if (isRedirect(httpResponse)) {
            // extract next location IRI
            getRedirectLocation(location, httpResponse) match {
              case Failure(exception) =>
                error = Some(SimplifiedException(exception.getClass.getName, exception.getMessage))
              case Success(value) =>
                location = value
                followedIRIs.append(("redirect", location))
                redirectCount += 1

                // check host is allowed to follow
                val host = location.toURI.getHost
                if (conf.hostFilter.tryHost(host).accepted) {
                  // check redirect limit
                  if (redirectCount <= conf.maxRedirects) {
                    doNextRequest = true
                  } else {
                    error = Some(SimplifiedException(classOf[MaxRedirectsError].getName, s"max redirects $redirectCount reached"))
                  }
                } else {
                  error = Some(SimplifiedException(classOf[HostNotAllowedException].getName, s"$host is not allowed"))
                }
            }
          }

          // handle alternate links
          val allAlternateLinks = getAlternateLinks(httpResponse)
          val bestAlternateLink = getBestAlternateLinks(location, allAlternateLinks)
          if (bestAlternateLink.isDefined) {
            // extract next location IRI
            location = bestAlternateLink.get
            followedIRIs.append(("alternate", location))
            redirectCount += 1

            // check host is allowed to follow
            val host = location.toURI.getHost
            if (conf.hostFilter.tryHost(host).accepted) {
              // check redirect limit
              if (redirectCount <= conf.maxRedirects) {
                doNextRequest = true
              } else {
                error = Some(SimplifiedException(classOf[MaxRedirectsError].getName, s"max redirects $redirectCount reached"))
              }
            } else {
              error = Some(SimplifiedException(classOf[HostNotAllowedException].getName, s"$host is not allowed"))
            }
          }

          // extract headers
          val header = extractHeader(httpResponse)
          headers.append(header)

          // check for retry delay
          val retryAfterHeader = getRetryAfterHeader(httpResponse)
          if (retryAfterHeader.isPresent) {
            val delay = HttpUtils.parseRetryAfterHeaderToDelayMillis(
              responseTimestamp, // TODO get better current time
              retryAfterHeader.get()
            )
            log.trace(s"FOUND Retry-After ${delay}ms")
            if (conf.minRequestDelay < delay && delay <= (conf.maxRequestDelay - conf.minRequestDelay)) {
              doNextRequest = true
              Thread.sleep(delay)
            } else if (conf.minRequestDelay < delay) {
              error = Some(SimplifiedException(classOf[RetryAfterToLongError].getName, s"retry-after $delay > ${conf.maxRequestDelay}"))
              doNextRequest = false
            }
          }

          if (doNextRequest) {
            val currentTimestamp = System.currentTimeMillis()
            val timePassedSinceLastResponse = currentTimestamp - responseTimestamp
            if (timePassedSinceLastResponse < conf.minRequestDelay) {
              Thread.sleep(conf.minRequestDelay - timePassedSinceLastResponse)
            }
          }
      }
    }

    if (null != httpResponse) {
      statusCode = httpResponse.statusCode()
      if (200 == statusCode) {
        try {
          // TODO own impl to not fully download (maybe own input stream)
          body = IOUtils.toByteArray(httpResponse.body())
          if (body.length > conf.maxResourceSize) {
            error = Some(SimplifiedException(
              classOf[ResourceSizeException].getName,
              s"data ${body.length} > ${conf.maxResourceSize} limit"
            ))
            body = null
          }
        } catch {
          case ioException: IOException =>
            error = Some(SimplifiedException(ioException.getClass.getName, ioException.getMessage))
        }
      }
    }

    HttpExchangeResult(
      iri = iri.normalize(true).locationIRI,
      statusCode = statusCode,
      headers = headers.toList,
      body = body,
      followedIRIs.toList,
      error,
      startTimestamp,
      System.currentTimeMillis() - startTimestamp
    )
  }

  def getBestAlternateLinks(iri: IRI, referenceAndType: List[(String, String)]): Option[IRI] = {
    val referenceByType = referenceAndType.map(t2 => t2._2 -> t2._1).toMap[String, String]
    val typeCandidates = referenceByType.keySet
    conf.mimeTypePreference.find(typeCandidates.contains).flatMap({
      selectedType =>
        Try { // TODO error is currently not delegated into results
          val location = referenceByType(selectedType)
          IRI.apply(new URL(iri.toURI.toURL, location).toURI, normalize = true)
        }.toOption
    })
  }

  private final val uriReferencePattern = "<([^<>]+)>".r

  def extractUriReference(entry: String): Option[String] = {
    uriReferencePattern.findFirstIn(entry)
  }

  private final val typePattern = "type=\"([^\"]+)\"".r

  def extractType(entry: String): Option[String] = {
    typePattern.findFirstIn(entry)
  }

  // link: </docs/jsonldcontext.jsonld>; rel="alternate"; type="application/ld+json"
  private def getAlternateLinks(httpResponse: JavaHttpResponse[InputStream]): List[(String, String)] = {
    val sc = httpResponse.statusCode()
    val (mimetype, _) = getTypeAndCharSet(extractHeader(httpResponse))
    if (sc == 200 && !isTargetMimeType(mimetype)) {
      log.trace(s"CHECK ALTERNATE LINKS ${httpResponse.uri()}")
      httpResponse.headers().allValues("link").asScala.flatMap({
        linkHeaderEntry =>
          linkHeaderEntry.split(",").filter(_.matches(".*rel=\"alternate\".*")).flatMap({
            alternateLinkEntry =>
              val reference = extractUriReference(alternateLinkEntry)
              val mimeType = extractType(alternateLinkEntry)
              if (reference.isDefined && mimeType.isDefined) {
                Some(reference.get.drop(1).dropRight(1), mimeType.get.split(";").head.trim.drop(6).dropRight(1))
              } else {
                None
              }
          })
      }).toList
    } else {
      List()
    }
  }

  private def isTargetMimeType(mimeType: Option[String]): Boolean = {
    mimeType match {
      case Some(value) =>
        conf.mimeTypePreference.contains(value)
      case None =>
        false
    }
  }

  private def getRetryAfterHeader(httpResponse: JavaHttpResponse[InputStream]): Optional[String] = {
    // todo check 503, 429, 301
    httpResponse.headers().firstValue("retry-after")
  }

  // can be merged link for the alternate Links
  private def isRedirect(httpResponse: JavaHttpResponse[InputStream]): Boolean = {
    val statusCode = httpResponse.statusCode()
    statusCode >= 300 && statusCode < 400
  }

  // TODO change to Option[IRI]
  private def getRedirectLocation(sourceIRI: IRI, httpResponse: JavaHttpResponse[InputStream]): Try[IRI] = Try {
    // todo httpResponse.uri().toURL
    val location = httpResponse.headers().firstValue("location").get()
    IRI.apply(new URL(sourceIRI.toURI.toURL, location).toURI, normalize = true)
  }

  private def extractHeader(httpResponse: JavaHttpResponse[InputStream]): util.Map[String, util.List[String]] = {
    httpResponse.headers().map()
  }

  private def get(iri: IRI, unsecure: Boolean = false): Try[JavaHttpResponse[InputStream]] = Try {
    log.trace(s"HTTP GET $iri")
    val request =
      HttpRequest.newBuilder(iri.toURI)
        .GET()
        .timeout(Duration.ofSeconds(conf.timeout))
        .header("accept", conf.acceptHeader)
        .header("user-agent", conf.userAgentHeader)
        .build()

    httpClient.send(request, JavaHttpResponse.BodyHandlers.ofInputStream())
  }

  def getTypeAndCharSet(header: util.Map[String, util.List[String]]): (Option[String], Option[String]) = {
    val wrapped = {
      header.asScala.get("content-type").flatMap[(String, String)]({
        ctHeaders =>
          ctHeaders.asScala.headOption.map[(String, String)]({
            ctHeader =>
              val ctSplit = ctHeader.split(";")
              if (ctSplit.length == 1) {
                (ctSplit(0), null)
              } else {
                val possibleCharset = ctSplit.tail.toList.find(_.trim.startsWith("charset"))
                (ctSplit(0), if (possibleCharset.isDefined) possibleCharset.get.split("=").last.trim else null)
              }
          })
      })
    }
    wrapped match {
      case Some(value) =>
        if (value._2 != null) {
          (Some(value._1), Some(value._2))
        } else {
          (Some(value._1), None)
        }
      case None => (None, None)
    }
  }
}
