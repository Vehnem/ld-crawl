package org.dbpedia.ld.fetch

import org.dbpedia.ldr.core.db.postgres.PostgresDataStore
import org.dbpedia.ldr.core.metadata.old.IriCollection
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import picocli.CommandLine.{Command, Option}

import java.io.File
import java.util
import java.util.concurrent.Callable
import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.io.Source

@Component
@Command(name = "fetch", mixinStandardHelpOptions = true)
class CLI(
  services: FetchServices,
  fetchConfig: FetchConfig
) extends Callable[Integer] {

  val groupingSize = 10000

  @Option(names = Array("--parallel"), required = true)
  var parallel: Int = 0

  @Option(names = Array("--seedFilter"), required = false)
  var seedFilter: String = null

  @Option(names = Array("--skipExisting"), required = false)
  var skipExisting: Boolean = false

  @Option(names = Array("--retryStatus"), required = false)
  var retryStatus: Int = Integer.MIN_VALUE

  @Option(names = Array("--hostList"), required = false, split = ",")
  var hostList: util.ArrayList[String] = new util.ArrayList[String]()

  @Option(names = Array("--hostListPath"), required = false)
  var hostListPath: String = null
  @Option(names = Array("--hostIrisDir"))
  var hostIrisDir: String = null

  override def call(): Integer = {

    fetchConfig.seed = seedFilter
    fetchConfig.retryStatus = retryStatus
    fetchConfig.skipExisting = skipExisting

    loadWork() // TODO can be skipped by parameter



    while (services.workBalancer.numberOfHosts() > 0) {
      Thread.sleep(2000)
    }

    while (services.altWorkBalancer.remaining > 0) {
      Thread.sleep(2000)
    }

    //    val hostRunner: new HostRunner("host",List(),)

    services.writeBehindDataService.flush()
    0 // Success
  }

  def loadWork(): Unit = {
    if (null != hostIrisDir && hostIrisDir.nonEmpty && null != hostListPath && hostListPath.nonEmpty) {
      services.createAltThreads(parallel)
      loadFromFiles()
    } else {
      services.createThreads(parallel)
      loadFromCollection()
    }
  }

  def loadFromFiles(): Unit = {
    println(s"load work from $hostListPath and $hostIrisDir")
    val source = Source.fromFile(hostListPath)
    source.getLines().foreach({
      hostString =>
        val hostIriFile = new File(hostIrisDir, hostString)
        try {
          val hostIriSource = Source.fromFile(hostIriFile)
          val iris = hostIriSource.getLines().toList
          services.altWorkBalancer.addAltBatchJob(AltBatchJob(hostString, iris))
          hostIriSource.close()
        } catch {
          case ex: Exception =>
            println(s"failed loading ${hostString} @ ${hostIriFile.toString}")
        }
    })
    source.close()
  }

  def loadFromCollection(): Unit = {
    if (hostList.isEmpty) {
      println(s"load work for ${seedFilter}")
      services.iriCollection.findUniqueHostBySeed(seedFilter).asScala.foreach({
        host =>
          services.workBalancer.addHost(host)
      })
    } else {
      println(s"load work for ${seedFilter} and ${hostList.asScala.mkString("|")}")
      hostList.asScala.foreach({
        host =>
          services.workBalancer.addHost(host)
      })
    }
  }
}