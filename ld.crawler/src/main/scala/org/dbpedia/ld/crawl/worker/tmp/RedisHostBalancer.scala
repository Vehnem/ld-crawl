package org.dbpedia.ld.crawl.worker.tmp

import org.dbpedia.ld.crawl.config.CrawlerConfig
import org.redisson.api.{RMap, RedissonClient}

import scala.collection.JavaConverters.asScalaIteratorConverter

class RedisHostBalancer(redissonClient: RedissonClient, crawlerConfig: CrawlerConfig) extends WorkBalancer {

  // TODO worker heartbeat
  //  private def workerRegistry: RSetCache[String] = redissonClient.getSetCache[String]("workers")

  // TODO is required for multi server access
  //  private def lock = redissonClient.getLock("balancerLock")

  private def hostRegistry: RMap[String, Long] = redissonClient.getMap[String, Long]("hosts")

  private def blockedHosts: RMap[String, Long] = redissonClient.getMap[String, Long](s"blockedBy_${crawlerConfig.serverId}")
  blockedHosts.delete()

  def addHost(host: String): Unit = synchronized {
    hostRegistry.put(host, System.currentTimeMillis())
  }

  // TODO needed as synchronized?
  def removeHost(host: String): Unit = synchronized {
    hostRegistry.remove(host)
  }

  def numberOfHosts(): Integer = {
    hostRegistry.size()
  }

  def nextHost(): Option[String] = synchronized {
    hostRegistry.keySet().iterator().asScala.find({
      ! blockedHosts.keySet.contains(_)
    }) match {
      case Some(host) =>
        // TODO +1 is difficult
        blockedHosts.put(host,System.currentTimeMillis()+1)
        Some(host)
      case None =>
        None
    }
  }

  def hostDone(host: String): Unit = synchronized {
    if(hostRegistry.get(host) < blockedHosts.get(host)) {
      hostRegistry.remove(host)
    }
    blockedHosts.remove(host)
  }
}
