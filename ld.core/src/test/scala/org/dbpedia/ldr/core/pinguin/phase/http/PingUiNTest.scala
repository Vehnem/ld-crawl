//package org.dbpedia.ldr.core.pinguin.phase.http
//
//import ch.qos.logback.classic.{Level, Logger}
//import org.dbpedia.ldr.core.iri.IRI
//import org.dbpedia.ldr.core.pinguin.http.{HttpClientConfig, HttpClientJava, HttpExchangeResult}
//import org.dbpedia.ldr.core.pinguin.parse.idres.Id2EntityResolver
//import org.dbpedia.ldr.core.pinguin.parse.{JsonldContextCache, JsonldParser, LinkedDataParser, ParseResult}
//import org.scalatest.funsuite.AnyFunSuite
//import org.slf4j.LoggerFactory
//
//import scala.collection.JavaConverters.asScalaIteratorConverter
//
//class PingUiNTest extends AnyFunSuite {
//
//  private val id2EntityResolver = LoggerFactory.getLogger(classOf[Id2EntityResolver]).asInstanceOf[Logger]
//  id2EntityResolver.setLevel(Level.TRACE)
//
//  private val jsonldContextCache = LoggerFactory.getLogger(classOf[JsonldContextCache]).asInstanceOf[Logger]
//  jsonldContextCache.setLevel(Level.TRACE)a
//
//  private val httpClientLogger = LoggerFactory.getLogger(classOf[HttpClientJava]).asInstanceOf[Logger]
//  httpClientLogger.setLevel(Level.TRACE)
//
//  private val riotLogger = LoggerFactory.getLogger("org.apache.jena.riot").asInstanceOf[Logger]
//  riotLogger.setLevel(Level.TRACE)
//
//  implicit val httpClient = new HttpClientJava(new HttpClientConfig())
//
//
//  test("http Test") {
//
//    iriTest("http://dbpedia.org/resource/Leipzig")
//    iriTest("http://dbpedia.org/resource/Eisenach")
//    iriTest("http://schema.org")
//    iriTest("https://www.imdb.com/name/nm0000206/")
//    //    iriTest("http://dbpedia.org/resource/Eisenach")
//    //    iriTest("https://schema.org")
//    //    iriTest("http://dbpedia.org/DoesNotExist")
//    //    iriTest("http://example.doesnotexit/resource/Leipzig")
//    //    iriTest("http://eexampless.org/resource/Leipzig")
//  }
//
//  def iriTest(iriString: String)(implicit httpClientJava: HttpClientJava): HttpExchangeResult = {
//    val iri = IRI.apply(iriString, true)
//    val result = httpClientJava.request(iri)
//    println(result)
//    result
//  }
//
//  test("jsonld parser") {
////    println(requestAndParseIri("http://www.wikidata.org/entity/Q64"))
////    println(requestAndParseIri("http://www.bibsonomy.org/burst/uri/bibtexkey/conf/webdb/Bizer10/dblp"))
////    println(requestAndParseIri("https://www.imdb.com/name/nm0000206/"))
////    println(requestAndParseIri("https://www.imdb.com/name/nm0000230/"))
////    println(requestAndParseIri("http://dbpedia.org/resource/Leipzig"))
////    println(requestAndParseIri("http://dbpedia.org/"))
//  }
//
//  def requestAndParseIri(iriString: String)(implicit httpClientJava: HttpClientJava): ParseResult = {
//    val iri = IRI(iriString, normalize = true)
//    val fetchResult = httpClientJava.request(iri)
//
//    val parser = new LinkedDataParser()
//    parser.parse(iri = iri, mimetype = fetchResult.mimeType.orNull, charset = fetchResult.charSet.orNull, data = fetchResult.body)
//  }
//
//  test("ID -> Entity Mapping") {
////    println(testId2EntityMapping("http://dbpedia.org/resource/Leipzig"))
////    println(testId2EntityMapping("https://dbpedia.org/resource/Leipzig"))
////    println(testId2EntityMapping("http://www.wikidata.org/entity/Q64"))
//  }
//
//  def testId2EntityMapping(iriString: String)(implicit httpClientJava: HttpClientJava): ParseResult = {
//    val iri = IRI(iriString, normalize = true)
//    val fetchResult = httpClientJava.request(iri)
//
//    val parser = new LinkedDataParser()
//    parser.parseAndFindMapping(
//      iri = iri,
//      mimetype = fetchResult.mimeType.orNull,
//      charset = fetchResult.charSet.orNull,
//      data = fetchResult.body,
//      fetchResult.followedIRIs.map(_._2)
//    )
//  }
//}
