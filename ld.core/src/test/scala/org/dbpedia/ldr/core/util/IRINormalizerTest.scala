//package org.dbpedia.ldr.core.util
//
//import org.apache.jena.irix.IRIProviderJenaIRI.IRIxJena
//import org.apache.jena.irix.IRIx
//import org.dbpedia.ldr.core.iri.IRI
//import org.scalatest.funsuite.AnyFunSuite
//
//import java.net.{URI, URLEncoder}
//import java.nio.charset.StandardCharsets
//import scala.util.{Failure, Success, Try}
//
//class IRINormalizerTest extends AnyFunSuite {
//
//  val testIris = List(
//    "http://dbpedia.org:80/resource/./Path?c=x&b=y&a=z&c=w#Fragment", // "http://dbpedia.org/resource/Path?a=z&b=y&c=w&c=x#Fragment"),
//    "https://dbpedia.org:1443/resource/./Path?c=x&b=y&a=z&c=w#Fragment", // "https://dbpedia.org:1443/resource/Path?a=z&b=y&c=w&c=x#Fragment"),
//    "https://blogs.sapo.pt/userinfo.bml?user=amantedosorriso", // "https://blogs.sapo.pt/userinfo.bml?user=amantedosorriso"),
//    "http://example.org/ros&eacute",
//    "http://example.org/ros&#233",
//    "http://example.org/ros&#xE9",
//    "http://example.org/rosÉ",
//    "http://example.org/ros\u263B",
//    "http://example.org:80",
//    "example://a/b/c/%7Bfoo%7D/ros&#xE9",
//    "eXAMPLE://a/./b/../b/%63/%7bfoo%7d/ros%c3%A9",
//    "eXAMPLE://a/./b/../b/%63/%7bfoo%7d/ros%c3%A9/",
//    "eXAMPLE://a/./b/../b/%63/%7bfoo%7d/ros%c3%A9/.",
//    "eXAMPLE://a/./b/../b/%63//%7bfoo%7d/ros%c3%a9/..",
//    "eXAMPLE://a/./b/../b/%63//\u0151/ros%c3%a9/..",
////    "http://statistics.data.gov.uk/id/county?name=Greater London",
//    "http://statistics.data.gov.uk/id/county?name=Greater%20London",
//    "https://en.wiktionary.org/wiki/Ῥόδος",
//    "https://en.wiktionary.org/wiki/%E1%BF%AC%CF%8C%CE%B4%CE%BF%CF%82",
//    "http://ru.dbpedia.org/resource/%D0%91%D0%B5%D1%80%D0%BB%D0%B8%D0%BD"
//  )
//
//  test("iri norm") {
//    testIris.foreach({
//      iristring =>
//        Try {
//          println(IRIx.create(iristring).toString)
//                    val iri = IRI.apply(iristring, normalize = true)
//              println(iristring,iri.toString, iri.toURI.toString, new URI(iristring).normalize().toString)
//        } match {
//          case Failure(exception) => exception.printStackTrace()
//          case Success(value) =>
//        }
//    })
//  }
//
//}
