//package org.dbpedia.ldr.core.cli
//
//import java.io.{File, FileInputStream, FileWriter, InputStream}
//import java.nio.charset.StandardCharsets
//import scala.io.Source
//import cats.effect.IO
//import fs2.{Pure, Stream, io, text}
//
//import scala.concurrent.ExecutionContext
//
//object ParseIRIs {
//
//  def main(args: Array[String]): Unit = {
//
//    val source = Source.fromFile(args(0), StandardCharsets.UTF_8.name())
//
//    val fw = new FileWriter(args(0), StandardCharsets.UTF_8)
//
//    //    source.getLines().par
//
//    source.close()
//    fw.close()
//
//  }
//
//  protected val ByteInputBufferSize: Int = 32 * 1024 //64 * 1024
//  protected val chunk: Int = 10000
//
//  def some(inputStream: InputStream): Unit = {
//
//    implicit val executionContext: ExecutionContext =
//      scala.concurrent.ExecutionContext.Implicits.global
//
//    val is: InputStream = new FileInputStream(new File("/tmp/my-file.mf"))
//
//    io.readInputStream(IO(is), chunk)
//      .through(text.utf8.decode)
//      .through(text.lines)
//      .parEvalMap(Runtime.getRuntime.availableProcessors())(
//        x => {
//          x
//        }
//      )
//
//    //    implicit val contextShift: ContextShift[IO] =
//    //      IO.contextShift(executionContext)
//  }
//}
