package org.dbpedia.ldr.core.filter

import inet.ipaddr.IPAddressString
import org.dbpedia.ldr.core.SimplifiedException
import org.dbpedia.ldr.core.dns.{DnsCache, DnsResolver}

import java.net.InetAddress


class IpBasedHostFilter(dnsCache: DnsCache) extends HostFilter {

  override def tryHost(host: String): HostFilterResult = {

    val ipAddressString = new IPAddressString(host)
    if (ipAddressString.isIPAddress) {
      if (isBlackList(ipAddressString.toAddress().toInetAddress)) {
        HostFilterResult(accepted = false, error = Some(SimplifiedException(new HostNotAllowedException(s"$host blacklisted"))))
      } else {
        HostFilterResult(accepted = true, error = None)
      }
    } else {
      val dnsResult = dnsCache.resolve(host)
      if (dnsResult.error.isDefined) {
        HostFilterResult(accepted = true, error = dnsResult.error)
      } else {
        HostFilterResult(accepted = true, error = None)
      }
    }
  }

  def isBlackList(inetAddress: InetAddress): Boolean = {
    inetAddress.isSiteLocalAddress
  }
}
