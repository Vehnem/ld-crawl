package org.dbpedia.ld.crawl.store

import org.apache.commons.dbcp2.BasicDataSource
import org.dbpedia.ldr.core.db.postgres.PostgresDataStore
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

import scala.collection.mutable.ListBuffer

@Service
class DataStoreService(
  postgresDataStore: PostgresDataStore
) {

  private val log = LoggerFactory.getLogger(classOf[DataStoreService])

  val buffer = new ListBuffer[(String,Long,Array[Byte])]()

  def addToBuffer(row: (String,Long,Array[Byte])): Unit = synchronized {
    buffer.append(row)
  }

  def writeBehind(row: (String, Long, Array[Byte])): Unit = synchronized {
    buffer.append(row)
    if(getBufferSize > 100) {
      log.info(s"start writing ${getBufferSize} to DB")
      postgresDataStore.saveRawSets(buffer.toList)
      buffer.clear()
    }
  }

  def writeToDb(): Unit = synchronized {
    log.info(s"start writing ${getBufferSize} to DB")
    postgresDataStore.saveRawSets(buffer.toList)
    buffer.clear()
  }

  def getBufferSize: Int = {
    buffer.size
  }

  def findRawData(id: String): Option[(Long,Array[Byte])] = synchronized {
    postgresDataStore.findRaw(id: String)
  }

  def findTimestamp(id: String): Option[Long] = {
    postgresDataStore.findRawTimestamp(id)
  }
}
