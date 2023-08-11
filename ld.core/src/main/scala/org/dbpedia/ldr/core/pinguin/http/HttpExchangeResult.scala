package org.dbpedia.ldr.core.pinguin.http

import org.dbpedia.ldr.core.SimplifiedException
import org.dbpedia.ldr.core.iri.IRI

import java.util
import scala.collection.JavaConverters.{collectionAsScalaIterableConverter, mapAsScalaMapConverter}

case class HttpExchangeResult(
  iri: IRI,
  statusCode: Int,
  headers: List[util.Map[String, util.List[String]]], // TODO change to TreeMap
  body: Array[Byte],
  followedIRIs: List[(String,IRI)],
  error: Option[SimplifiedException],
  start: Long,
  duration: Long,
) {

  // TODO better and is redundant with code in HttpClientJava
  lazy val (mimeType, charSet): (Option[String], Option[String]) = {
    val wrapped = {
      headers.lastOption.flatMap({
        header =>
          header.asScala.get("content-type").flatMap[(String, String)]({
            ctHeaders =>
              ctHeaders.asScala.headOption.map[(String, String)]({
                ctHeader =>
                  val ctSplit = ctHeader.split(";")
                  if (ctSplit.length == 1) {
                    (ctSplit(0), null)
                  } else {
                    val possibleCharset = ctSplit.tail.toList.find(_.trim.startsWith("charset"))
                    (ctSplit(0), if(possibleCharset.isDefined) possibleCharset.get.split("=").last.trim else null)
                  }
              })
          })
      })
    }
    wrapped match {
      case Some(value) =>
        if(value._2 != null) {
          (Some(value._1),Some(value._2))
        } else {
          (Some(value._1),None)
        }
      case None => (None, None)
    }
  }

  lazy val bodySize: Option[Long] = {
    if(null != body) {
      Some(body.length)
    } else {
      None
    }
  }

  override def toString: String = {
    val sb = new StringBuilder()
    sb.append("HttpExchangeResult(\n")
    sb.append(s"  iri = $iri\n")
    sb.append(s"  statusCode = $statusCode\n")
    sb.append(s"  headers = [")
    headers.foreach({
      header =>
        sb.append(s"\n    $header")
    })
    sb.append("]\n")
    sb.append(s"  body.length = ${if (null != body) body.length else 0}\n")
    sb.append(s"  followedIRIs = [")
    followedIRIs.foreach({
      followedIRI =>
        sb.append(s"\n    $followedIRI")
    })
    sb.append("]\n")
    sb.append(s"  mimeType = ${if (mimeType.isDefined) mimeType.get}\n")
    sb.append(s"  charSet = ${if (charSet.isDefined) charSet.get}\n")
    sb.append(s"  error = $error\n")
    sb.append(s"  start = $start\n")
    sb.append(s"  duration = $duration\n")
    sb.append(")\n")
    sb.toString()
  }
}
