package org.dbpedia.ld.crawl.test.loadredis

import ch.qos.logback.classic.{Level, Logger}
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.slf4j.LoggerFactory
import org.dbpedia.ldr.core.iri.IRI

import java.net.URI
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Files, Paths}
import scala.util.{Failure, Success, Try}
import scala.jdk.CollectionConverters.*

class IRILoader

object IRILoader {

  private val log = LoggerFactory.getLogger(classOf[IRILoader])

  def main(args: Array[String]): Unit = {

    val redissonLogger = LoggerFactory.getLogger("org.redisson.command").asInstanceOf[Logger]
    redissonLogger.setLevel(Level.WARN)

    val config = new Config()
    config.useSingleServer().setAddress("redis://127.0.0.1:6379")

    val client = Redisson.create(config)

    val knownIris = client.getSet[String]("knownIris")

    val irisFilePath = "/home/marvin/data/all.txt.sort"

    var processCnt, errorCnt = 0

    val cs = StandardCharsets.UTF_8 // "ISO-8859-1 | UTF-8"

    Files.lines(Paths.get(irisFilePath), cs).iterator().asScala.grouped(1000).foreach({
      batch =>
        val payload = batch.flatMap(line => {

          processCnt += 1
          if ((processCnt % 10000) == 0) println(processCnt)

          Try {
            val iri = IRI(new URI(line), false)
            val fragment = iri.fragment
            val niri = iri.normalize()
//            niri.fragment = fragment
            niri.toString
          } match {
            case Failure(exception) =>
              log.error(line + " " + exception.getClass.getName + " " + exception.getMessage)
              errorCnt += 1
              None
            case Success(value) =>
              Some(value)
          }
        })
        knownIris.addAll(payload.asJava)
    })
    client.shutdown()
    println(s"loaded ${processCnt-errorCnt}/$processCnt")
  }
}
