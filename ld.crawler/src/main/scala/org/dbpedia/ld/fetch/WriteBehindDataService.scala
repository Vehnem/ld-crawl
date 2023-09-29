package org.dbpedia.ld.fetch

import org.dbpedia.ldr.core.db.postgres.PostgresDataStore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import scala.collection.mutable.ListBuffer

@Service
class WriteBehindDataService(
  dataStore: PostgresDataStore
) {

  @Value("${ldr.data.writeBehindLimit:100}")
  var writeBehindLimit: Integer = 100

  private val log = LoggerFactory.getLogger(classOf[WriteBehindDataService])

  private val buffer: ListBuffer[FetchDataRow] = new ListBuffer[FetchDataRow]()

  dataStore.createRawTable()

  def write(row: FetchDataRow): Unit = synchronized {
    buffer.append(row)
    if(buffer.size >= writeBehindLimit) {
      writeCurrentBuffer()
    }
  }

  def flush(): Unit = synchronized {
    writeCurrentBuffer()
  }

  private def writeCurrentBuffer(): Unit = {
    log.info(s"start writing ${buffer.size} to DB")
    try {
      dataStore.saveRawSets(buffer.toList.map(_.to3Tuple))
    } catch {
      case ex: Exception =>
        log.warn(s"failed writing batch ${buffer.size} to DB: ${ex.getClass.getName}: ${ex.getMessage}")
        log.warn(s"fallback to single row inserts for current batch")
        buffer.foreach(writeBufferEntry)
    }
    buffer.clear()
  }

  private def writeBufferEntry(row: FetchDataRow): Unit = {
    try {
      dataStore.saveRaw(row.iri,row.timestamp,row.data)
    } catch {
      case ex: Exception =>
        log.error(s"failed writing <${row.iri}>: ${ex.getClass.getName}: ${ex.getMessage}")
    }
  }
}
