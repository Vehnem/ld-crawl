package org.dbpedia.ldr.core

import ch.qos.logback.classic.{Level, Logger}
import org.apache.commons.lang3.time.StopWatch
import org.dbpedia.ldr.core.util.HashUtil
import org.redisson.Redisson
import org.redisson.config.Config
import org.slf4j.LoggerFactory

object RedTest {

  def main(args: Array[String]): Unit = {

    val redisLog = LoggerFactory.getLogger("org.redisson").asInstanceOf[Logger]
    redisLog.setLevel(Level.WARN)

    val config = new Config();
    config.useSingleServer().setAddress("redis://127.0.0.1:6379");

    val redisson = Redisson.create(config);

    val rf = new RedissonFunctions(redisson)

    val prefix = "http://example.org/"

    val sw = new StopWatch()
    sw.start()

    val prepared =
    (0 until 1e4.toInt).map({
      idx =>
        val iri = prefix+idx
        val hash = HashUtil.hexMd5sum(iri)
        val group = hash.substring(0,2)
        (group, iri)
    })
    sw.split()
    println(sw.formatSplitTime())

    prepared.grouped(1000).foreach({
      batch =>
        rf.addIris(batch.toList)
    })

    sw.split()
    println(sw.formatSplitTime())
    sw.stop()

    redisson.shutdown()
  }

}
