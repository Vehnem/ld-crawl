//package org.dbpedia.ldr.core.util
//
//import org.apache.commons.io.IOUtils
//import org.apache.commons.io.output.ByteArrayOutputStream
//import org.scalatest.funsuite.AnyFunSuite
//
//import java.io.ByteArrayInputStream
//import java.nio.charset.StandardCharsets
//
//class CompressionTest extends AnyFunSuite {
//
//  test("Array[Byte] compression") {
//
//    val bytea = "Some Test String".getBytes(StandardCharsets.UTF_8)
//
//    val cBytea = GZUtil.toCompressedByteArray(bytea)
//
//    val uBytea = GZUtil.toUncompressedByteArray(cBytea)
//
//    println(new String(uBytea))
//  }
//}
