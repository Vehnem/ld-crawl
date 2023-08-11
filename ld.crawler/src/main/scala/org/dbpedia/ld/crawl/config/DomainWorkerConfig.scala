package org.dbpedia.ld.crawl.config

//import org.dbpedia.ld.crawl.dns.DnsCacheService

import org.dbpedia.ld.crawl.dns.DnsCacheService
import org.dbpedia.ld.crawl.store.DataStoreService
import org.dbpedia.ld.crawl.worker.tmp.RedisHostBalancer
import org.dbpedia.ldr.core.metadata.old.{FetchResultCollection, IriCollection}
//import org.dbpedia.ldr.core.metadata.{FetchResultCollection, IriCollection}
import org.springframework.stereotype.Service

import scala.beans.BeanProperty

@Service
class DomainWorkerConfig(
  val redisHostBalancer: RedisHostBalancer,
  val seedURIRepo: IriCollection,
  val requestedResourceRepo: FetchResultCollection,
  val dataStoreService: DataStoreService,
  val dnsService: DnsCacheService,
  val crawlerConfig: CrawlerConfig
) {

  @BeanProperty
  var seedFilter: String = null

}
