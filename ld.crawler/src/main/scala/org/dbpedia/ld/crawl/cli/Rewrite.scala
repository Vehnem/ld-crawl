//package org.dbpedia.ld.crawl.cli
//
//import org.dbpedia.ldr.core.iri.IRI
//import org.dbpedia.ldr.core.metadata.old.FetchResultCollection
//import org.dbpedia.ldr.core.monitor.ThroughPutWatch
//import org.springframework.data.mongodb.core.MongoTemplate
//import org.springframework.stereotype.Component
//import picocli.CommandLine.Command
//
//import java.util.concurrent.Callable
//import scala.collection.JavaConverters.{asJavaIterableConverter, iterableAsScalaIterableConverter}
//
//@Component
//@Command(name = "rewrite", mixinStandardHelpOptions = true)
//class Rewrite(
//  mongoTemplate: MongoTemplate,
//  fetchResultCollection: FetchResultCollection
//) extends Callable[Int] {
//
//  override def call(): Int = {
//
//    val list = fetchResultCollection.findAll()
//    println("collected docs in memory")
//
//    val tw = new ThroughPutWatch
//    tw.start()
//
//    list.asScala.map({
//      doc =>
//        doc.setHost(IRI.apply(doc.location, normalize = true).host)
//        doc
//    }).grouped(1000).foreach({
//      batch =>
//        tw.increase(1000)
//        tw.get()
//        fetchResultCollection.saveAll(batch.asJava)
//    })
//
//    println(s"${tw.get()} per/secs")
//
//    0 //
//  }
//}
