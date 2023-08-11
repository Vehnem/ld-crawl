//package org.dbpedia.ldr.core.util
//
//import org.scalatest.funsuite.AnyFunSuite
//
//import scala.util.{Failure, Success}
//
//class ClientTest extends AnyFunSuite {
//
//  val uris = List(
//    "http://capitaldatabase.referata.com/wiki/Special:ExportRDF/08648", // HttpConnectTimeout
//    "http://capitaldatabase.referata.com/wiki/Special:ExportRDF/08837", // ConnectException
//    "http://capitaldatabase.referata.com/wiki/Special:ExportRDF/08648",  // now ConnectException
//    "http://http://164.100.24.167/"
//  )
//
////  test("testing exceptions") {
////
////    val ldc = new LDClientPingUiNImpl(new LDClientConfig)
////
////    uris.foreach({
////      uri =>
////        ldc.resolve(uri) match {
////          case Failure(exception) =>
////            exception.printStackTrace(System.out)
////            exception.getCause.printStackTrace(System.out)
////            exception.getCause.getCause.printStackTrace(System.out)
////          case Success(value) =>
////            println(value.statusCode)
////        }
////        println("==============================================")
////    })
////
////  }
//}
