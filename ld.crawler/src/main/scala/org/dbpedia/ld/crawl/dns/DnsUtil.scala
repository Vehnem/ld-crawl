package org.dbpedia.ld.crawl.dns

import java.net.InetAddress
import scala.util.{Failure, Success, Try}

object DnsUtil {

  def resolve(host: String, timeoutMillis: Int): DnsEntry = {
    Try {
      InetAddress.getByName(host)
    } match {
      case Failure(exception) =>
        DnsEntry(null, null, exception.getClass.getName, exception.getMessage)
      case Success(inetAddress: InetAddress) =>
        if (isBlacklistedInetAddress(inetAddress)) {
          // TODO change into exception
          DnsEntry(inetAddress.getAddress, inetAddress.toString, "org.dbpedia.ld.crawl.dns.BlacklistError", s"${inetAddress} is blacklisted")
          // TODO ping is making problems
          //        } else if (!isReachableInetAddress(inetAddress, timeoutMillis)) {
          //          DnsEntry(inetAddress.getAddress, inetAddress.toString, "unreachable")
        } else {
          DnsEntry(inetAddress.getAddress, inetAddress.toString, null, null)
        }
    }
  }

  def isReachableInetAddress(inetAddress: InetAddress, timeoutMillis: Int): Boolean = {
    inetAddress.isReachable(timeoutMillis)
  }

  def isBlacklistedInetAddress(inetAddress: InetAddress): Boolean = {
    inetAddress.isSiteLocalAddress
  }

  def main(args: Array[String]): Unit = {

  }
}
