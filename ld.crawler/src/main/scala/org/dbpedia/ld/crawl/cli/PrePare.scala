//package org.dbpedia.ld.crawl.cli
//
//import com.mongodb.client.model.{UpdateOneModel, UpdateOptions}
//import org.bson.Document
//import org.bson.BsonDocumentReader
//import org.dbpedia.ldr.core.iri.IRI
//import org.dbpedia.ldr.core.metadata.{IriCollection, IriDoc}
//import org.dbpedia.ldr.core.monitor.ThroughPutWatch
//import org.dbpedia.ldr.core.util.HashUtil
//import org.springframework.data.mongodb.core.query.{Criteria, Query, Update}
//import org.springframework.data.mongodb.core.{MongoOperations, MongoTemplate}
//import org.springframework.stereotype.Component
//import picocli.CommandLine.{Command, Option}
//
//import java.net.URI
//import java.nio.charset.StandardCharsets
//import java.util
//import java.util.concurrent.Callable
//import scala.collection.JavaConverters.seqAsJavaListConverter
//import scala.io.Source
//
//@Component
//@Command(name = "prepare", mixinStandardHelpOptions = true)
//class PrePare(
//  mongoTemplate: MongoTemplate,
//) extends Callable[Integer] {
//
//  // TODO make this global
//  private val groupingSize = 10000
//
//  @Option(names = Array("--seedPath"), required = true)
//  private var seedPath: String = ""
//
//  @Option(names = Array("--seedName"), required = true)
//  private var seedName: String = ""
//
//  @Option(names = Array("--charset"), required = false, description = Array("ISO-8859-1 | UTF-8"))
//  private var charset: String = "UTF-8"
//
//  @Option(names = Array("--batchSize"), required = false)
//  private var batchSize: Integer = 1000
//
//  override def call(): Integer = {
//
//    val tw = new ThroughPutWatch()
//    tw.start()
//
//    // TODO other sources
//    val seedSource = Source.fromFile(seedPath, charset)
//
//    val collection = mongoTemplate.getDb.getCollection("iriDoc")
//
//    seedSource.getLines().map({
//      possibleURI =>
//        getDocument(possibleURI, seedName)
//    }).grouped(batchSize).foreach({
//      docGroup =>
//        collection.bulkWrite(
//          docGroup.map({
//            doc =>
//              val docMap = doc.toMap
//              docMap.remove("_id")
//              docMap.remove("seed")
//
//              val updateDoc = new Document()
//              updateDoc.put("$set", docMap)
//              updateDoc.put("$addToSet", new Document("seed", seedName))
//
//              new UpdateOneModel(
//                new Document("_id", doc.iri),
//                updateDoc,
//                new UpdateOptions().upsert(true)
//              )
//          }).asJava
//        )
//        tw.increase(docGroup.length)
//        tw.get()
//    })
//
//    seedSource.close()
//    println(tw.get() + " inserts/second")
//    0 // Success
//  }
//
//  def getDocument(iriString: String, seedName: String): IriDoc = {
//    val doc = new IriDoc()
//    try {
//      val iri = IRI.apply(iriString, normalize = true)
//      val normalizedIRIString = iri.toString
//      doc.setIri(normalizedIRIString)
//      doc.setHash(HashUtil.hexSha256sum(normalizedIRIString))
//      val locationIRIString = iri.locationIRI.toString
//      doc.setLocation(locationIRIString)
//      doc.setLocationHash(HashUtil.hexSha256sum(locationIRIString))
//      doc.setHost(iri.host)
//      val seeds = new util.ArrayList[String]()
//      seeds.add(seedName)
//      doc.setSeed(seeds)
//    } catch {
//      case e: Exception =>
//        doc.setIri(iriString)
//        doc.setError(e.getClass.getName)
//        doc.setErrorMessage(e.getMessage)
//    }
//    doc
//  }
//}
