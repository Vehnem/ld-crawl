package org.dbpedia.ld.crawl.crawler

import io.minio.MinioClient

class Minio(minioConfig: MinioConfig) {

  private val minioClient = MinioClient.builder()
}
