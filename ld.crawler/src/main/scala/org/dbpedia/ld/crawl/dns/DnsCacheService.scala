package org.dbpedia.ld.crawl.dns

import org.apache.commons.lang3.concurrent.TimedSemaphore
import org.dbpedia.ld.crawl.config.DnsCacheConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

//TODO add parameters as @Config
@Service
class DnsCacheService(
  dnsCache: DnsCache,
  dnsCacheConfig: DnsCacheConfig
) {
  private val log = LoggerFactory.getLogger(classOf[DnsCacheService])

  private val semaphore = new TimedSemaphore(1, TimeUnit.SECONDS, dnsCacheConfig.requestsPerSecond)

  private val timeout = 10000

  private val ttl = dnsCacheConfig.ttl

  //  private val ip4Regex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$".r

  def resolveAndTry(host: String): DnsEntry = {
    val possibleEntry = dnsCache.get(host)
    if (null == possibleEntry) {
      semaphore.acquire()
      val dnsEntry = DnsUtil.resolve(host, timeout)
      dnsCache.put(host, dnsEntry, ttl, TimeUnit.SECONDS)
      dnsEntry
    } else {
      possibleEntry
    }
  }
}
