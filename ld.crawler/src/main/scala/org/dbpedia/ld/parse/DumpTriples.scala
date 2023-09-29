package org.dbpedia.ld.parse

//import org.apache.catalina.core.StandardContext
import org.bson.Document
import org.dbpedia.ldr.core.db.postgres.PostgresDataStore
import org.dbpedia.ldr.core.monitor.ThroughPutWatch
import org.dbpedia.ldr.core.util.GZUtil
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import picocli.CommandLine.{Command, Option}

import java.io.{File, FileOutputStream, FileWriter, OutputStream}
import java.nio.charset.StandardCharsets
import java.util.concurrent.Callable
import scala.collection.JavaConverters.{asScalaIteratorConverter, iterableAsScalaIterableConverter}

@Component
@Command(name = "dump", mixinStandardHelpOptions = true)
class DumpTriples(
  mongoTemplate: MongoTemplate,
  dataStore: PostgresDataStore
) extends Callable[Int] {

  @Option(names = Array("--out"), required = true)
  var out: File = null

  @Option(names = Array("--gz"), required = false)
  var gz: Boolean = false

  override def call(): Int = {
    val coll = mongoTemplate.getCollection("parseResultDoc")
    val findIterable = coll.find(new Document("tripleCount", new Document("$gt", 0)))

    val os = buildOs()

    val tw = new ThroughPutWatch()
    tw.start()

    var count = 0
    findIterable.iterator().asScala.foreach({
      doc =>
        try {
          val possibleData = dataStore.findParsed(doc.getString("locationHash"))
          if(possibleData.isDefined) {
            val bytes = GZUtil.toUncompressedByteArray(possibleData.get._2)
            os.write(bytes)
            os.flush()
          }
          count += 1
          tw.increase(1)
        } catch {
          case ex: Exception =>
            ex.printStackTrace()
        }
        if(count % 1000 == 0) tw.get()
    })

    println(tw.get()+" ops")

    os.flush()
    os.close()

    println(s"dumped $count documents")
    0 // SUCCESS
  }

  def buildOs(): OutputStream = {
    if(gz) {
      GZUtil.toCompressedOutputStream(new FileOutputStream(out))
    } else {
      new FileOutputStream(out)
    }
  }

  /*
          services.fetchResultCollection.findUniqueHost().asScala.foreach({
            host =>
              services.workBalancer.addHost(host)
          })
   */
}
