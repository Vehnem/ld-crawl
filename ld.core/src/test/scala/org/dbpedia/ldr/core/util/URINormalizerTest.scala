//package org.dbpedia.ldr.core.util
//
//import org.apache.jena.irix.{IRIProviderJDK, IRIx, SystemIRIx}
//import org.scalatest.funsuite.AnyFunSuite
//
//import java.net.{URI, URL}
//import java.util
//
//class URINormalizerTest extends AnyFunSuite {
//
//  val testSetnormalize = List(
//    ("http://dbpedia.org:80/resource/./Path?c=x&b=y&a=z&c=w#Fragment", "http://dbpedia.org/resource/Path?a=z&b=y&c=w&c=x#Fragment"),
//    ("https://dbpedia.org:1443/resource/./Path?c=x&b=y&a=z&c=w#Fragment", "https://dbpedia.org:1443/resource/Path?a=z&b=y&c=w&c=x#Fragment"),
//    ("https://blogs.sapo.pt/userinfo.bml?user=amantedosorriso", "https://blogs.sapo.pt/userinfo.bml?user=amantedosorriso"),
//    //    ("http://dbpedia.org/resource/Angela%20Merkel")
//  )
//
//  /*
//  "http://example.org/ros&eacute;" (in HTML),
//   "http://example.org/ros&#233"; (in HTML or XML), and
//   "http://example.org/ros&#xE9"; (in HTML or XML) are all resolved into
//   what is denoted in this document (see section 1.4) as
//   "http://example.org/ros&#xE9"; (the "&#xE9;" here standing for the
//   actual e-acute character, to compensate for the fact that this
//   document cannot contain non-ASCII characters).
//   */
//  val l = List(
//    "http://example.org/ros&eacute",
//    "http://example.org/ros&#233",
//    "http://example.org/ros&#xE9",
//    "http://example.org/rosÉ",
//    "example://a/b/c/%7Bfoo%7D/ros&#xE9",
//    "eXAMPLE://a/./b/../b/%63/%7bfoo%7d/ros%c3%A9"
//  )
//
//  test("test") {
//
//
//
////    val provider = SystemIRIx.getProvider
//
//    SystemIRIx.setProvider(new IRIProviderJDK)
//
//    l.map({
//      string =>
//        val iri = IRIx.create(string)
//        iri.normalize()
////        iri.equals()
//    }).foreach({
//      iri => println(iri.toString)
//    })
//
//
//
//    //    l.map(new URI(_).normalize()).foreach(println)
//
//    //    l.map(r.create(_)).foreach(x => println(x.normalize(true).toString))
//  }
//
////  test("normalize") {
////    testSetnormalize.foreach({
////      entry =>
////        val nURI = UriNormalizer.normalizeWithFragment(new URI(entry._1))
////        assert(
////          nURI.toString == entry._2, "normalization issue"
////        )
////        println(s"✓ ${entry._1} => $nURI")
////    })
////  }
//
//  val testSetlocation = List(
//    ("http://example.org/startlocation", "/target/location.nt", "http://example.org/target/location.nt"),
//    ("http://example.org/path/startlocation", "target/location.nt", "http://example.org/path/target/location.nt"),
//    ("http://blogs.sapo.pt/userinfo.bml?user=amantedosorriso", "https://blogs.sapo.pt/userinfo.bml?user=amantedosorriso", "https://blogs.sapo.pt/userinfo.bml?user=amantedosorriso")
//  )
//
//
//  test("location Header resolution") {
//
//    testSetlocation.foreach({
//      testEntry =>
//        val requestURI = testEntry._1
//        val locationURI = testEntry._2
//        val expectedResolvedURI = testEntry._3
//        val resolvedURI = new URL(new URI(requestURI).toURL, locationURI)
//        assert(expectedResolvedURI == resolvedURI.toString, "unexpected resolved URI")
//        println(s"✓ $requestURI + $locationURI => ${resolvedURI.toString}")
//    })
//  }
//
//  val defaultPortMap = new util.HashMap[String,Integer]()
//  defaultPortMap.put("http",80)
//  defaultPortMap.put("https",443)
//
//  test("squirrel normalizer") {
//
//
//  }
//
//  //  test("temporary") {
//  //    testSetlocation.foreach({
//  //      testEntry =>
//  //        val requestURI = testEntry._1
//  //        val locationURI = testEntry._2
//  //        val expectedResolvedURI = testEntry._3
//  //        val resolvedURI = resolve(UriNormalizer.parseURI(requestURI).get,locationURI)
//  //        assert(expectedResolvedURI == resolvedURI.toString, "unexpected resolved URI")
//  //        println(s"✓ $requestURI + $locationURI => ${resolvedURI.toString}")
//  //    })
//  //
//  //  }
//  //
//  //  def resolve(currentURI: URI,locationValue: String): URI = {
//  //    val newURL = new URL(currentURI.toURL,locationValue).toURI
//  //    val newURI = UriNormalizer.normalize(newURL)
//  //    println(newURI.toString)
//  //    newURI
//  //  }
//}
