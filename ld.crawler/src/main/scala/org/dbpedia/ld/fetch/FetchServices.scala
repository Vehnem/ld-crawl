package org.dbpedia.ld.fetch

import org.dbpedia.ldr.core.balancer.WorkBalancer
import org.dbpedia.ldr.core.filter.HostFilter
import org.dbpedia.ldr.core.metadata.old.{FetchResultCollection, IriCollection}
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service

import scala.collection.mutable.ListBuffer

@Service
class FetchServices(
  val fetchConfig: FetchConfig,
  val hostFilter: HostFilter,
  val workBalancer: WorkBalancer,
  val iriCollection: IriCollection,
  val fetchResultCollection: FetchResultCollection,
  val writeBehindDataService: WriteBehindDataService,
  val mongoTemplate: MongoTemplate,
  val altWorkBalancer: AltWorkBalancer
) {

  private val log = LoggerFactory.getLogger(classOf[FetchServices])

  private val pool = ListBuffer[Thread]()

  def threadStatus(): Unit = {
    log.info("pool status "+pool.count(_.isAlive)+"/"+pool.length+" alive")
  }

  def createThreads(number: Int): Unit = {
    (0 until number).foreach({
      _ =>
        val thread = new Thread(new FetchThread(fetchConfig, this))
        pool.append(thread)
        thread.start()
    })
  }

  def createAltThreads(number: Int): Unit = {
    (0 until number).foreach({
      _ =>
        val thread = new Thread(new FetchThreadAlt(fetchConfig, this))
        pool.append(thread)
        thread.start()
    })
  }
}
