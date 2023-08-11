//package org.dbpedia.ld.crawl.cli
//
//import org.dbpedia.ld.crawl.parser.ParserThread
//import org.dbpedia.ld.crawl.worker.tmp.RedisJournalService
//import org.dbpedia.ldr.core.data.PostgresDataStore
//import org.dbpedia.ldr.core.metadata.{FetchResultCollection, ParseResultCollection}
//import org.springframework.data.mongodb.core.MongoTemplate
//import org.springframework.stereotype.Component
//import picocli.CommandLine.{Command, Option}
//
//import java.util.concurrent.Callable
//import scala.collection.JavaConverters.{iterableAsScalaIterableConverter, seqAsJavaListConverter}
//
//@Component
//@Command(name = "parse", mixinStandardHelpOptions = true)
//class Parse(
//  mongoTemplate: MongoTemplate,
//  fetchJournal: FetchResultCollection,
//  redisJournalService: RedisJournalService,
//  dataStore: PostgresDataStore,
//  parseJournal: ParseResultCollection
//) extends Callable[Int] {
//
//  @Option(names = Array("--parallel"), required = true)
//  var parallel: Int = 0
//
//  override def call(): Int = {
//
//    dataStore.createParsedTable()
//
//    fetchJournal.findByStatusCode("200").asScala.map({
//      fetchMD =>
//        (fetchMD.uri,
//        fetchMD.contentType)
//    }).grouped(10000).foreach({
//      group =>
//        redisJournalService.parsable.addAll(group.toList.asJava)
//    })
//
//    (0 until parallel).foreach({
//      _ =>
//        val thread = new Thread(new ParserThread(redisJournalService,parseJournal,dataStore))
//        thread.start()
//    })
//
//    while (! redisJournalService.parsable.isEmpty) {
//      Thread.sleep(5000)
//    }
//
//    0
//  }
//}
