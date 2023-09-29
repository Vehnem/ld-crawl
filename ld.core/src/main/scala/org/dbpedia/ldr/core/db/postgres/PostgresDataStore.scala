package org.dbpedia.ldr.core.db.postgres

import org.apache.commons.dbcp2.BasicDataSource
import org.springframework.stereotype.Service

import java.sql.{Connection, DriverManager}
import scala.util.{Failure, Success, Try}

@Service
class PostgresDataStore(
  config: PostgresDataStoreConfig
) {

  val ds = new BasicDataSource()
  ds.setUrl(config.url)
  ds.setUsername(config.user)
  ds.setPassword(config.password)
  ds.setMinIdle(10)
  ds.setMaxIdle(25)
  ds.setMaxTotal(50)
  ds.setMaxOpenPreparedStatements(100)

  def getConnection: Connection = {
    ds.getConnection
  }

  private final val RAWDATATABLE: String = "raw"
  private final val PARSEDDATATABLE: String = "parsed"

  private def createDataTable(table: String): Boolean = synchronized {
    val conn = getConnection
    val plainStatement =
      s"""CREATE TABLE IF NOT EXISTS $table (
         |  id CHAR(64) PRIMARY KEY,
         |  timestamp BIGINT NOT NULL,
         |  data BYTEA NOT NULL
         |);
         |ALTER Table $table ALTER COLUMN data SET STORAGE EXTERNAL;
         |""".stripMargin
    val value = conn.createStatement().execute(plainStatement)
    conn.close()
    value
  }

  def createRawTable(): Boolean = {
    createDataTable(RAWDATATABLE)
  }

  def createParsedTable(): Boolean = {
    createDataTable(PARSEDDATATABLE)
  }

  private def saveData(table: String, id: String, timestamp: Long, bytes: Array[Byte]): Unit = {
    val conn = getConnection
    // TODO on conflict
    val ps = conn.prepareStatement(s"INSERT INTO $table (id, timestamp, data) VALUES (?, ?, ?)")
    ps.setString(1, id)
    ps.setLong(2, timestamp)
    ps.setBytes(3, bytes)
    ps.executeUpdate()
    ps.close()
    conn.close()
  }

  def saveRaw(id: String, timestamp: Long, bytes: Array[Byte]): Unit = {
    saveData(RAWDATATABLE, id, timestamp, bytes)
  }

  def saveParsed(id: String, timestamp: Long, bytes: Array[Byte]): Unit = {
    saveData(PARSEDDATATABLE, id, timestamp, bytes)
  }

  def saveRawSets(rows: List[(String, Long, Array[Byte])]): Int = {
    saveDataSets(rows, RAWDATATABLE)
  }

  def saveParsedSets(rows: List[(String, Long, Array[Byte])]): Int = {
    saveDataSets(rows, PARSEDDATATABLE)
  }

  // TODO
  private def saveDataSets(rows: List[(String, Long, Array[Byte])], tableName: String): Int = {
    val conn = getConnection
    val INSERT_URI_SQL: String =
        s"""INSERT INTO $tableName (id, timestamp, data)
         |VALUES (?, ?, ?)
         |ON CONFLICT (id) DO UPDATE
         |SET timestamp = EXCLUDED.timestamp, data = EXCLUDED.data;
         |""".stripMargin

    val preparedStatement = conn.prepareStatement(INSERT_URI_SQL)
    conn.setAutoCommit(false)

    rows.foreach({
      tuple =>
        preparedStatement.setString(1, tuple._1)
        preparedStatement.setLong(2, tuple._2)
        preparedStatement.setBytes(3, tuple._3)
        preparedStatement.addBatch()
    })

    val updateCount = preparedStatement.executeBatch()
    conn.commit()
    conn.setAutoCommit(true)
    conn.close()
    updateCount.toList.sum
  }

  // TODO case class or serializable
  def findRaw(id: String): Option[(Long,Array[Byte])] = {
    val conn = getConnection
    val stmt = conn.createStatement()
    Try {
      val rs = stmt.executeQuery(s"SELECT timestamp, data FROM raw WHERE id = '$id'")
      rs.next()
      val timestamp = rs.getLong(1)
      val bytes = rs.getBytes(2)
      rs.close()
      (timestamp,bytes)
    } match {
      case Success(value) =>
        conn.close()
        Some(value)
      case Failure(exception) =>
        conn.close()
        println(exception.getClass.getName + " " + exception.getMessage)
        None
    }
  }

  def findParsed(id: String): Option[(Long,Array[Byte])] = {
    val conn = getConnection
    val stmt = conn.createStatement()
    Try {
      val rs = stmt.executeQuery(s"SELECT timestamp, data FROM parsed WHERE id = '$id'")
      rs.next()
      val timestamp = rs.getLong(1)
      val bytes = rs.getBytes(2)
      rs.close()
      (timestamp,bytes)
    } match {
      case Success(value) =>
        conn.close()
        Some(value)
      case Failure(exception) =>
        conn.close()
        println(exception.getClass.getName + " " + exception.getMessage)
        None
    }
  }

  def findRawTimestamp(id: String): Option[Long] = {
    val conn = getConnection
    val stmt = conn.createStatement()
    Try {
      val rs = stmt.executeQuery(s"SELECT timestamp FROM raw WHERE id = '$id'")
      rs.next()
      val timestamp = rs.getLong(1)
      rs.close()
      timestamp
    } match {
      case Success(value) =>
        conn.close()
        Some(value)
      case Failure(exception) =>
        conn.close()
        println(exception.getClass.getName + " " + exception.getMessage)
        None
    }
  }
}
