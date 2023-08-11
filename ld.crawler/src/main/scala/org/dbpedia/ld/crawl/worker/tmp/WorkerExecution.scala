package org.dbpedia.ld.crawl.worker.tmp

import org.dbpedia.ld.crawl.config.DomainWorkerConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

import scala.collection.mutable.ListBuffer

// TODO synchronize
@Service
class WorkerExecution(
  workerConfig: DomainWorkerConfig,
) {

  private val log = LoggerFactory.getLogger(classOf[WorkerExecution])

  private val pool = ListBuffer[Thread]()

  def createOrAddWorkerThreads(size: Integer): Unit = {

    (0 until size).foreach({
      _ =>
//        val thread = new Thread(new DomainWorker(workerConfig))
//        pool.append(thread)
//        thread.start()
    })
  }

  def checkStatus(): Unit = {
    log.info("pool status "+pool.count(_.isAlive)+"/"+pool.length+" alive")
  }
}
