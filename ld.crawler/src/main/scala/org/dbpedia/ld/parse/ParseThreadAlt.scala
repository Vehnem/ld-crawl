package org.dbpedia.ld.parse

import org.apache.jena.rdf.model.Model
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.dbpedia.ldr.core.iri.IRI
import org.dbpedia.ldr.core.metadata.old.{FetchResultDoc, ParseResultDoc}
import org.dbpedia.ldr.core.pinguin.parse.LinkedDataParser
import org.dbpedia.ldr.core.util.GZUtil
import org.slf4j.LoggerFactory

import java.io.{ByteArrayOutputStream, Closeable}
import scala.collection.JavaConverters.iterableAsScalaIterableConverter

class ParseThreadAlt(
  conf: ParseConfig,
  parseServices: ParseServices
) extends Runnable with Closeable {

  private val log = LoggerFactory.getLogger(classOf[ParseThread])

  private var continue: Boolean = true

  private val parser = new LinkedDataParser

  override def run(): Unit = {
    while (continue) {
      val job = parseServices.altWorkBalancer.getNextAltBatchJob
//      val fetchResultDoc = FetchResultDoc.fromDocument(job.doc)
//      _process(IRI.apply(fetchResultDoc.location, normalize = true), fetchResultDoc)
    }
    //    while (continue) {
    //      var host: Option[String] = parseServices.workBalancer.nextHost()
    //      while (host.isEmpty) {
    //        host = parseServices.workBalancer.nextHost()
    //        Thread.sleep(1000)
    //      }
    //      processHost(host.get)
    //      parseServices.workBalancer.hostDone(host.get)
    //    }
  }

  private def processHost(host: String): Unit = {
    log.info(s"processing $host")
    val fetchResultDocs = parseServices.fetchResultCollection.findByHostAndStatusCode(host, 200)
    processResultDocs(host, fetchResultDocs)
  }

  private def processResultDocs(host: String, docs: java.util.List[FetchResultDoc]): Unit = {
    var skipped = 0
    docs.asScala.foreach({
      doc =>
        log.debug(s"processing ${doc.location}")
        val iri = IRI.apply(doc.location, normalize = true)
        if (conf.skipExisting) {
          val parseResultDoc = parseServices.parseResultCollection.findById(iri.toString)
          if (parseResultDoc.isEmpty) {
            _process(iri, doc)
          } else {
            skipped += 1
          }
        } else {
          _process(iri, doc)
        }
    })
    log.info(s"done $host skipped ${skipped} of ${docs.size()}")
  }

  def _process(iri: IRI, doc: FetchResultDoc): Unit = {
    parseServices.dataStore.findRaw(doc.locationHash) match {
      case Some(data) =>
        processData(
          iri, doc.contentType, doc.charset, data._2,
          doc.followedIri.asScala.toArray.map(_.get("value")).flatMap({
            x =>
              try {
                Some(IRI.apply(x, normalize = true))
              } catch {
                case ex: Exception =>
                  println(s"ERROR IRI.apply $x")
                  ex.printStackTrace()
                  None
              }
          })
            .toList
        )
      case None =>
        log.info(s"skipping $iri no data found in db")
    }
  }

  def processData(iri: IRI, mimetype: String, charset: String, bytes: Array[Byte], followedIRIs: List[IRI]): Unit = {

    val doc = new ParseResultDoc()
    doc.location = (iri.toString)
    doc.locationHash = (iri.sha256sum)
    doc.host = (iri.host)

    val uncompressedBytes = GZUtil.toUncompressedByteArray(bytes)
    val parseResult = parser.parseAndFindMapping(
      iri, mimetype, charset, uncompressedBytes,
      followedIRIs
    )
    parseResult.error match {
      case Some(parseError) =>
        doc.errorClass = (parseError.className)
        doc.errorMessage = (parseError.msg)
      case None =>
        doc.tripleCount = (parseResult.model.size())
        if (parseResult.cdiri.isDefined) doc.realIri = (parseResult.cdiri.get.toString)
        val modelBytes = modelToBytes(parseResult.model)
        parseServices.writeBehindDataService.write(
          ParseResultRow(iri.sha256sum,
            System.currentTimeMillis(),
            GZUtil.toCompressedByteArray(modelBytes)
          )
        )
    }
    doc.typeInformation = (parseResult.typeInformation)
    parseServices.parseResultCollection.save(doc)
  }

  def modelToBytes(model: Model): Array[Byte] = {
    val bos = new ByteArrayOutputStream()
    RDFDataMgr.write(bos, model, Lang.NTRIPLES)
    bos.toByteArray
  }

  override def close(): Unit = {
    continue = false
  }
}
