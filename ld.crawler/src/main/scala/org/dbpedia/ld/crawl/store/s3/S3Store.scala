package org.dbpedia.ld.crawl.store.s3

import io.minio.{MinioClient, PutObjectArgs, UploadObjectArgs}
import org.dbpedia.ld.crawl.DTO
import org.dbpedia.ld.crawl.store.Store

import java.io.{ByteArrayInputStream, InputStream}

class S3Store(config: S3Config) extends Store[DTO] {

  private val s3client = MinioClient.builder()
    .endpoint("http://localhost:9000")
    .credentials("tQCLky8N4taL5l1vg9Tf","G7vnGPnT4M0hqaI1tmw5aDLpeVnIQAxxUGhzLtpZ")
    .build()

  override def save(doc: DTO): Boolean = {

    val (is, oLen, pLen) = bytea2StreamObject(doc.`object`)

    val objectWriteResponse = s3client.putObject(
      PutObjectArgs.builder()
        .contentType("application/octet-stream")
        .`object`(doc.key)
        .stream(is,oLen,pLen)
        .bucket("test")
        .build()
    )


      true
  }

  private def bytea2StreamObject(bytea: Array[Byte]): (InputStream,Long,Long) = {
    val objeLen = bytea.length
    val partLen = 1000 * 1000 * 10

    (new ByteArrayInputStream(bytea), objeLen, partLen)
  }

  override def find(id: String): DTO = {
    new DTO(null,null)
  }

  override def delete: Boolean = {
    true
  }
}
