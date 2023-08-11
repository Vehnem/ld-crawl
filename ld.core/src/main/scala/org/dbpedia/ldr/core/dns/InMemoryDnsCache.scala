package org.dbpedia.ldr.core.dns

import java.util.concurrent.ConcurrentHashMap

class InMemoryDnsCache(dnsResolver: DnsResolver) extends DnsCache {

  private val cache = new ConcurrentHashMap[String, DnsResult]()

  override def resolve(host: String): DnsResult = {
    if(cache.containsKey(host)) {
      cache.get(host)
    } else {
      val dnsResult = dnsResolver.resolve(host)
      cache.put(host, dnsResult)
      dnsResult
    }
  }
}
