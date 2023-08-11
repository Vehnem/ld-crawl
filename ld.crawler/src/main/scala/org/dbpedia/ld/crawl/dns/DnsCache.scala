package org.dbpedia.ld.crawl.dns

import java.util.concurrent.TimeUnit

trait DnsCache {

  def get(host: String): DnsEntry
  def put(host: String, dnsEntry: DnsEntry, ttl: Int, timeUnit: TimeUnit): Unit
}
