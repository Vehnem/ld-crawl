package org.dbpedia.ld.crawl.store.cassandra

import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

import java.util.UUID

@Repository
trait TestRepository extends CassandraRepository[TestEntity,String] {

}
