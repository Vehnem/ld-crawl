package org.dbpedia.ld.crawl.store.cassandra

import com.datastax.oss.driver.api.core.uuid.Uuids
import org.springframework.data.cassandra.core.cql.{Ordering, PrimaryKeyType}
import org.springframework.data.cassandra.core.mapping.*
import org.springframework.data.cassandra.core.mapping.CassandraType.Name

import java.util
import java.util.UUID
import scala.beans.BeanProperty

@Table
class TestEntity extends Serializable {

  @BeanProperty
  @PrimaryKey
  var id: String = _

  @BeanProperty
  @CassandraType(`type` = Name.BLOB)
  var bytea: Array[Byte] = _
}
