//package org.dbpedia.ldr.core.pinguin.phase.http
//
//import ch.qos.logback.classic.{Level, Logger}
//import org.dbpedia.ldr.core.pinguin.http.{HttpClientJava, HttpUtils}
//import org.scalatest.funsuite.AnyFunSuite
//import org.slf4j.LoggerFactory
//
//import java.io.StringReader
//
//class HttpUtilsTest extends AnyFunSuite {
//
////  test("Parse Retry-After Http Header to Millis") {
////
////    val Integer = HttpUtils.parseRetryAfterHeaderToDelayMillis(System.currentTimeMillis(), "Wed, 21 Oct 2015 07:28:00")
////
////  }
//
//  test("upickle Test") {
//
//    val json1 = ujson.read("""{ "some" : "string" }""")
//    println(json1("some").value.isInstanceOf[String])
//    val json2 = ujson.read("""{ "some" : { "foo" : "string"} }""")
//    println(json2("some").value.isInstanceOf[String])
//
//
//
//  }
//
//  test("extracAlternateLink") {
//    val header = Map[String,List[String]](
//      "link" -> List(
//        "</docs/jsonldcontext.jsonld>; rel=\"alternate\"; type=\"application/ld+json\", </docs/jsonldcontext.ttl>; rel=\"alternate\"; type=\"text/turtle\"",
//        "</docs/jsonldcontext.jsonld; rel=\"alternate\"; type=\"application/ld+json\"",
//        "</docs/jsonldcontext.jsonld>; rel=\"alternate\"",
//        "<http://example.org>; rel=\"preconnet\""
//      )
//    )
//
//    header.get("link").get.flatMap({
//      linkHeaderEntry =>
//        linkHeaderEntry.split(",").filter(_.matches(".*rel=\"alternate\".*")).flatMap({
//          alternateLinkEntry =>
//            val reference = extractUriReference(alternateLinkEntry)
//            val mimeType = extractType(alternateLinkEntry)
//            if(reference.isDefined && mimeType.isDefined) {
//              Some(reference.get.drop(1).dropRight(1), mimeType.get.split(";").head.trim.drop(6).dropRight(1))
//            } else {
//              None
//            }
//        })
//    }).foreach(println)
//
//  }
//
//  test("retry-after") {
//    println(HttpUtils.parseRetryAfterHeaderToDelayMillis(System.currentTimeMillis(),"0"))
//    println(HttpUtils.parseRetryAfterHeaderToDelayMillis(System.currentTimeMillis(),"5"))
//  }
//
//  private final val uriReferencePattern = "<([^<>]+)>".r
//
//  def extractUriReference(entry: String): Option[String] = {
//    uriReferencePattern.findFirstIn(entry)
//  }
//
//  private final val typePattern = "type=\"([^\"]+)\"".r
//
//  def extractType(entry: String): Option[String] = {
//    typePattern.findFirstIn(entry)
//  }
//
//}
