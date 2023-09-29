package org.dbpedia.ldr.core

import ch.qos.logback.classic.{Level, Logger}
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.slf4j.LoggerFactory

object CrawlerTest {


  def main(args: Array[String]): Unit = {


    val redisLog = LoggerFactory.getLogger("org.redisson").asInstanceOf[Logger]
    redisLog.setLevel(Level.WARN)

    val config = new Config();
    config.useSingleServer().setAddress("redis://127.0.0.1:6379");

    val redisson = Redisson.create(config);
    val rf = new RedissonFunctions(redisson)

    (0 until 3).foreach({
      idx =>
        val t = new Thread(new Crawler(rf))
        t.start()
    })
  }

  class Crawler(redissonFunctions: RedissonFunctions) extends Runnable {

    val log = LoggerFactory.getLogger(classOf[Crawler])

    override def run(): Unit = {
      while(true) {
        val group_opt = redissonFunctions.nextGroup()
        if(group_opt.isEmpty) {
          // TODO blocking?
          log.info("wait for new groups")
          Thread.sleep(1000)
        }
        else {
          val group = group_opt.get
          println(s"start processing group $group")
          var iri = redissonFunctions.nextIri(group)
          while (iri.isDefined) {
            // TODO something with IRI
            iri = redissonFunctions.nextIri(group)
          }
//          redissonFunctions.releaseGroup(group)
          println(s"finished group $group")
        }
      }
    }
  }
}
