//package org.dbpedia.ld.crawl.test
//
////import ch.qos.logback.classic.{Level, Logger}
//import org.apache.commons.lang3.time.StopWatch
//import org.dbpedia.ld.crawl.DTO
//import org.dbpedia.ld.crawl.store.s3.{S3Config, S3Store}
//import org.dbpedia.ldr.core.iri.IRI
//import org.dbpedia.ldr.core.util.HashUtil
//import org.redisson.Redisson
//import org.redisson.config.Config
//import org.slf4j.LoggerFactory
//
//import java.io.FileWriter
//import java.net.URI
//import java.nio.charset.StandardCharsets
//import java.nio.file.{Files, Paths}
//import java.util.concurrent.LinkedBlockingQueue
//import java.util.concurrent.atomic.AtomicInteger
//import scala.jdk.CollectionConverters.*
//import scala.util.{Failure, Success, Try}
//
//object MinioLoader {
//
//  var processCnt = new AtomicInteger()
//
//  def main(args: Array[String]): Unit = {
//
//
//
////    val redissonLogger = LoggerFactory.getLogger("org.redisson.command").asInstanceOf[Logger]
////    redissonLogger.setLevel(Level.WARN)
//
////    val config = new Config()
////    config.useSingleServer().setConnectionPoolSize(120).setAddress("redis://127.0.0.1:6379")
//
////    val client = Redisson.create(config)
////    val queue = client.getBlockingQueue[String]("queue")
//
//    val cs = StandardCharsets.UTF_8 // "ISO-8859-1 | UTF-8"
//
//    val bq = new LinkedBlockingQueue[String]()
//    (0 until 1000000).foreach({
//      idx =>
//        bq.put(s"test/${idx}")
//    })
//
//    println("loaded queue")
//
//    val sw = new StopWatch()
//    sw.start()
//
//    (0 until 100).foreach({
//      idx =>
//        val thread = new Thread(new Runnable {
//          val store = new S3Store(new S3Config("", "",""))
//
//          override def run(): Unit = {
//            while( true ) {
//              val iriStr = bq.take()
//              store.save(DTO(iriStr, "test".getBytes(cs)))
//              ak()
//            }
//          }
//        })
//        thread.start()
//    })
//
////    var loadCnt, errorCnt = 0
//    val irisFilePath = "/home/marvin/data/all.txt.sort"
//
////    val fw = new FileWriter("1kk_iris.txt")
////
////    Files.lines(Paths.get(irisFilePath), cs).iterator().asScala.slice(0,1000000).grouped(10000).foreach({
////      lines =>
////        val batch = lines.flatMap({
////          line =>
////            loadCnt += 1
////            if (loadCnt % 10000 == 0) println(loadCnt)
////            Try {
////              IRI(new URI(line), true)
////            } match
////              case Failure(exception) =>
////                // todo
////                errorCnt += 1
////                None
////              case Success(value) =>
////                Some(getKey(value))
////                try {
////                  Some(getKey(value))
////                } catch {
////                  case ex: Exception =>
////                    println(line)
////                    ex.printStackTrace()
////                    throw ex
////                }
////        })
////        fw.write(batch.mkString("\n")+"\n")
////        fw.flush()
//
////        queue.addAll(batch.asJava)
////    })
//
////    fw.close()
////
////    println(s"loadCnt: ${loadCnt - errorCnt}/{$loadCnt}")
//
//    while (!bq.isEmpty) {
//      Thread.sleep(1000)
//    }
//    ak(true)
//
////    client.shutdown()
//
//    sw.split()
//    println(sw.formatSplitTime())
//  }
//
//  def ak(fin: Boolean = false) = {
//    val cnt = processCnt.incrementAndGet()
//    if (cnt % 10000 == 0)
//      println(s"processCnt: $cnt")
//    if (fin)
//      println(s"processCnt: $cnt")
//  }
//
//  def getKey(iri: IRI): String = {
//    val sb = new StringBuilder()
//    sb.append(iri.scheme)
//    sb.append("/")
//    sb.append(iri.authority)
//    sb.append("/")
//    sb.append(HashUtil.hexSha256sum(iri.toString))
//    sb.toString()
//  }
//}
