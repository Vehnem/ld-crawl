package org.dbpedia.ld.parse

import org.dbpedia.ldr.core.balancer.WorkBalancer
import org.dbpedia.ldr.core.data.PostgresDataStore
import org.dbpedia.ldr.core.metadata.old.{FetchResultCollection, IriCollection, ParseResultCollection}
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

import scala.collection.mutable.ListBuffer

@Service
class ParseServices(
  val parseConfig: ParseConfig,
  val workBalancer: WorkBalancer,
  val fetchResultCollection: FetchResultCollection,
  val dataStore: PostgresDataStore,
  val writeBehindDataService: WriteBehindDataService,
  val parseResultCollection: ParseResultCollection,
  val iriCollection: IriCollection,
  val altWorkBalancer: AltWorkBalancer
) {

  private val log = LoggerFactory.getLogger(classOf[ParseServices])

  private val pool = ListBuffer[Thread]()

  def threadStatus(): Unit = {
    log.info("pool status "+pool.count(_.isAlive)+"/"+pool.length+" alive")
  }

  def createThreads(number: Int): Unit = {
    (0 until number).foreach({
      _ =>
        val thread = new Thread(new ParseThread(parseConfig, this))
        pool.append(thread)
        thread.start()
    })
  }

  def createAltThreads(number: Int): Unit = {
    (0 until number).foreach({
      _ =>
        val thread = new Thread(new ParseThreadAlt(parseConfig, this))
        pool.append(thread)
        thread.start()
    })
  }
}
