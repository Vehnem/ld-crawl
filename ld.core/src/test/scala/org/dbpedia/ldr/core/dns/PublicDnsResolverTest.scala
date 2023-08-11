//package org.dbpedia.ldr.core.dns
//
//import org.scalatest.funsuite.AnyFunSuite
//
//class PublicDnsResolverTest extends AnyFunSuite {
//
//  test("main") {
//
//    implicit val dnsResolver = new PublicDnsResolver
//    resolveHost("dbpedia.org")
//    resolveHost("dbpediaaa.org")
//    resolveHost("127.0.0.1")
//  }
//
//  def resolveHost(host:String)(implicit dnsResolver: PublicDnsResolver): Unit = {
//    val dnsResult = dnsResolver.resolve(host)
//    println(dnsResult)
//  }
//}
