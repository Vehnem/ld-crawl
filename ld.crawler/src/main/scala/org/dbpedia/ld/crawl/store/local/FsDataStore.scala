//package org.dbpedia.ldr.core.data
//
//import org.apache.commons.compress.compressors.{CompressorOutputStream, CompressorStreamFactory}
//import org.apache.commons.io.IOUtils
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.stereotype.Service
//
//import java.io._
//import java.net.URI
//import java.nio.charset.StandardCharsets
//import java.security.MessageDigest
//import scala.language.implicitConversions
//import org.dbpedia.ldr.core.util.HashUtil.hexSha256sum
//
//@Service
//class FsDataStore(
//  @Value("${store.base.dir:./data/www/}") baseDir: String
//) extends DataStore {
//
//  private val compression = CompressorStreamFactory.GZIP
//
//  private def toCompressedOutputStream(outputStream: OutputStream): CompressorOutputStream =
//    new CompressorStreamFactory()
//      .createCompressorOutputStream(compression, outputStream)
//
//  private def toUncompressedInputStream(inputStream: InputStream): InputStream =
//    new CompressorStreamFactory()
//      .createCompressorInputStream(inputStream)
//
//  private val baseFile = new File(baseDir)
//
//  def save(uri: String, bytes: Array[Byte], extension: String = ".fetched"): File = {
//    val file = getFile(uri, extension+".gz")
//    file.getParentFile.mkdirs()
//    val os: OutputStream = toCompressedOutputStream(new FileOutputStream(file))
//    // TODO
//    IOUtils.write(bytes,os)
//    os.close()
//    file
//  }
//
//  def find(uri: String, extension: String = ".fetched"): Option[InputStream] = {
//    val file = getFile(uri, extension+".gz")
//    if (!file.exists()) {
//      None
//    } else {
//      Option(toUncompressedInputStream(new FileInputStream(file)))
//    }
//  }
//
//  def getFile(uri: String, extension: String = ".fetched"): File = {
//    val nUri = new URI(uri.split("#").head).normalize()
////    val subPath = List(nUri.getScheme, nUri.getAuthority, nUri.getPath).mkString("/")
//    val subPath = List(nUri.getScheme, nUri.getAuthority).mkString("/")
//    val dir = new File(baseFile, subPath)
//    new File(dir, s"${hexSha256sum(nUri.toString)}$extension")
//  }
//
//
//}
