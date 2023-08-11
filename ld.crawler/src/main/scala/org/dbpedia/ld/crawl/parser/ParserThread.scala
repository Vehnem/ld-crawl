//package org.dbpedia.ld.crawl.parser
//
//import org.apache.commons.io.output.ByteArrayOutputStream
//import org.dbpedia.ldr.core.data.PostgresDataStore
//import org.dbpedia.ldr.core.metadata.{ParseResultCollection, ParseResultDoc}
//import org.dbpedia.ldr.core.util.{GZUtil, HashUtil}
//import org.dbpedia.ldx.io.{DataMgr, Format}
//
//import java.io.{ByteArrayInputStream, Closeable}
//import scala.util.{Failure, Success}
//
//class ParserThread(
//  redisJournalService: RedisJournalService,
//  parseJournal: ParseResultCollection,
//  postgresDataStore: PostgresDataStore
//) extends Runnable with Closeable {
//
//  private val parser = DataMgr
//
//  override def run(): Unit = {
//
//    while (true) {
//
//      val id_ct = redisJournalService.parsable.take()
//
//      postgresDataStore.findRaw(HashUtil.hexSha256sum(id_ct._1)) match {
//        case Some(value) =>
//          val is = GZUtil.toUncompressedInputStream(new ByteArrayInputStream(value._2))
//
//          parser.parse(is, Format.mimetoFormat.getOrElse(id_ct._2, Format.RDF_XML())) match {
//            case Failure(exception) =>
//              parseJournal.save(
//                new ParseResultDoc(
//                  id_ct._1,
//                  HashUtil.hexSha256sum(id_ct._1),
//                  exception.getClass.getName,
//                  exception.getMessage
//                )
//              )
//            case Success(model) =>
//              parseJournal.save(
//                new ParseResultDoc(
//                  id_ct._1,
//                  HashUtil.hexSha256sum(id_ct._1),
//                  null,
//                  null
//                )
//              )
//              val bos = new ByteArrayOutputStream()
//              val os = GZUtil.toCompressedOutputStream(bos)
//              parser.write(model, os, Format.RDF_NTRIPLES())
//              postgresDataStore.saveParsed(HashUtil.hexSha256sum(id_ct._1), System.currentTimeMillis(), bos.toByteArray)
//          }
//        case None =>
//      }
//    }
//  }
//
//  override def close(): Unit = {
//
//  }
//}
