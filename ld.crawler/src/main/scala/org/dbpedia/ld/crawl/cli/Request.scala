//package org.dbpedia.ld.crawl.cli
//
//import org.dbpedia.ld.crawl.config.DomainWorkerConfig
//import org.dbpedia.ld.crawl.store.DataStoreService
//import org.dbpedia.ld.crawl.worker.tmp.{RedisHostBalancer, WorkerExecution}
//import org.dbpedia.ldr.core.db.postgres.PostgresDataStore
//import org.dbpedia.ldr.core.metadata.old.IriCollection
//import org.springframework.stereotype.Component
//import picocli.CommandLine.{Command, Option}
//
//import java.util.concurrent.Callable
//import scala.collection.JavaConverters.iterableAsScalaIterableConverter
//
//@Component
//@Command(name = "request", mixinStandardHelpOptions = true)
//class Request(
//  dataStore: PostgresDataStore,
//  uriJournal: IriCollection,
//  dataStoreService: DataStoreService,
//  domainWorkerConfig: DomainWorkerConfig,
//  redisHostBalancer: RedisHostBalancer,
//  workerExecution: WorkerExecution
//) extends Callable[Integer] {
//
//  val groupingSize = 10000
//
//  @Option(names = Array("--parallel"), required = true)
//  var parallel: Int = 0
//
//  @Option(names = Array("--seedFilter"), required = false)
//  var seedFilter: String = null
//
//  override def call(): Integer = {
//
//    if(seedFilter != null) domainWorkerConfig.setSeedFilter(seedFilter)
//
//    println("get domain list")
//    uriJournal.findUniqueHost().asScala.foreach({
//      host =>
//        redisHostBalancer.addHost(host)
//    })
//
//    println("create raw data table if not exists")
//    dataStore.createRawTable()
//
//    println("starting worker threads")
//    workerExecution.createOrAddWorkerThreads(parallel)
//
//    println("waiting for crawler")
//    while (
//      redisHostBalancer.numberOfHosts() > 0
//    ) {
//      Thread.sleep(1000)
//    }
//
//    dataStoreService.writeToDb()
//
//    0 // Success
//  }
//}
