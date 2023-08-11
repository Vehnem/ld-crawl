package org.dbpedia.ldr.core.util

import org.apache.commons.compress.compressors.{CompressorOutputStream, CompressorStreamFactory}
import org.apache.commons.io.IOUtils
import org.apache.commons.io.output.ByteArrayOutputStream

import java.io.{ByteArrayInputStream, InputStream, OutputStream}

object GZUtil {

  private val compression = CompressorStreamFactory.GZIP

  def toCompressedOutputStream(outputStream: OutputStream): CompressorOutputStream =
    new CompressorStreamFactory()
      .createCompressorOutputStream(compression, outputStream)

  def toUncompressedInputStream(inputStream: InputStream): InputStream =
    new CompressorStreamFactory()
      .createCompressorInputStream(inputStream)

  def toCompressedByteArray(bytes: Array[Byte]): Array[Byte] = {

    val bos = new ByteArrayOutputStream()
    val os: OutputStream = toCompressedOutputStream(bos)
    // TODO
    IOUtils.write(bytes, os)
    os.close()
    bos.close()
    bos.toByteArray
  }

  def toUncompressedByteArray(bytes:Array[Byte]): Array[Byte] = {
    val bis = new ByteArrayInputStream(bytes)
    val is = toUncompressedInputStream(bis)
    val _bytes = IOUtils.toByteArray(is)
    bis.close()
    is.close()
    _bytes
  }
}
