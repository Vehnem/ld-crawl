package org.dbpedia.ld.crawl.test.queueiris

import ch.qos.logback.classic.{Level, Logger}
import org.redisson.Redisson
import org.redisson.config.Config
import org.slf4j.LoggerFactory

object QueueIris {

  def main(args: Array[String]): Unit = {

    val redissonLogger = LoggerFactory.getLogger("org.redisson.command").asInstanceOf[Logger]
    redissonLogger.setLevel(Level.WARN)

    val config = new Config()
    config.useSingleServer().setAddress("redis://127.0.0.1:6379")

    val client = Redisson.create(config)

    val knownIris = client.getSet[String]("knownIris")

    var iriCnt = 0
    knownIris.iterator().forEachRemaining(iri => {
      iriCnt += 1
      if(iriCnt % 10000 == 0) println(iriCnt)
    })

    println(iriCnt)
    client.shutdown()
  }
}
