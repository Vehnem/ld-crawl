package org.dbpedia.ld.crawl.dns

import org.redisson.api.RedissonClient

import java.util.concurrent.TimeUnit

class RedisDnsCache(redissonClient: RedissonClient) extends DnsCache {

  private val cache = redissonClient.getMapCache[String,DnsEntry]("dnsCache")

  override def get(host: String): DnsEntry = {
    cache.get(host)
  }

  override def put(host: String, dnsEntry: DnsEntry, ttl: Int, timeUnit: TimeUnit): Unit = {
    cache.put(host,dnsEntry,ttl,timeUnit)
  }
}
