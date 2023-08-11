package org.dbpedia.ld.parse

import org.bson.Document
import org.dbpedia.ld.fetch.AltBatchJob
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import picocli.CommandLine.{Command, Option}

import java.io.File
import java.util
import java.util.concurrent.Callable
import scala.collection.JavaConverters.{asScalaIteratorConverter, iterableAsScalaIterableConverter, seqAsJavaListConverter}

@Component
@Command(name = "parse", mixinStandardHelpOptions = true)
class ParseResults(
  services: ParseServices,
  parseConfig: ParseConfig,
  mongoTemplate: MongoTemplate
) extends Callable[Integer] {


  @Option(names = Array("--parallel"), required = true)
  var parallel: Int = 0

  @Option(names = Array("--seedFilter"), required = false)
  var seedFilter: String = null

  @Option(names = Array("--skipExisting"), required = false)
  var skipExisting: Boolean = false

  @Option(names = Array("--hostList"), required = false, split = ",")
  var hostList: util.ArrayList[String] = new util.ArrayList[String]()

  @Option(names = Array("--altWorker"), required = false)
  var userAltWorker: Boolean = false

  override def call(): Integer = {

    parseConfig.skipExisting = (skipExisting)

    loadWork()

    while (services.workBalancer.numberOfHosts() > 0) {
      Thread.sleep(2000)
    }

    while (services.altWorkBalancer.remaining > 0) {
      println(services.altWorkBalancer.remaining)
      Thread.sleep(2000)
    }

    services.writeBehindDataService.flush()
    services.threadStatus()

    0 // Success
  }

  def loadWork(): Unit = {
    if (userAltWorker) {
      println("using alt Worker")
      loadFromCollectionAlt()
      services.createAltThreads(parallel)
    } else {
      loadFromCollection()
      services.createThreads(parallel)
    }
  }

  private val coll = mongoTemplate.getCollection("fetchResultDoc")


  def loadFromCollectionAlt(): Unit = {
    coll.find(new Document("statusCode", 200)).iterator.asScala.grouped(1000).foreach({
      docs =>
            //todo
//        val altJobs = docs.map(AltBatchJob)
//        services.altWorkBalancer.addAltBatchJobs(altJobs.asJava)
    })
  }

  def loadFromCollection(): Unit = {
    if (hostList.isEmpty) {
      println(s"load work")
      if (null != seedFilter) {
        services.iriCollection.findUniqueHostBySeed(seedFilter).asScala.foreach({
          host =>
            services.workBalancer.addHost(host)
        })
      } else {
        services.fetchResultCollection.findUniqueHost().asScala.foreach({
          host =>
            services.workBalancer.addHost(host)
        })
      }
    } else {
      println(s"load work for ${seedFilter} and ${hostList.asScala.mkString("|")}")
      hostList.asScala.foreach({
        host =>
          services.workBalancer.addHost(host)
      })
    }
  }

  //  def loadFromFiles(): Unit = {
  //    println(s"load work from $hostListPath and $hostIrisDir")
  //    val source = Source.fromFile(hostListPath)
  //    source.getLines().foreach({
  //      hostString =>
  //        val hostIriFile = new File(hostIrisDir, hostString)
  //        try {
  //          val hostIriSource = Source.fromFile(hostIriFile)
  //          val iris = hostIriSource.getLines().toList
  //          services.altWorkBalancer.addAltBatchJob(AltBatchJob(hostString, iris))
  //          hostIriSource.close()
  //        } catch {
  //          case ex: Exception =>
  //            println(s"failed loading ${hostString} @ ${hostIriFile.toString}")
  //        }
  //    })
  //    source.close()
  //  }
}

