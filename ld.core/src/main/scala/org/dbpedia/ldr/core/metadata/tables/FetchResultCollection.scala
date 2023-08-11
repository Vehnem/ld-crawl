package org.dbpedia.ldr.core.metadata.old

import org.dbpedia.ldr.core.metadata.old.FetchResultDoc
import org.springframework.data.mongodb.repository.{Aggregation, MongoRepository}

import java.util.Optional

trait FetchResultCollection extends MongoRepository[FetchResultDoc, String] {

  def findByLocation(location: String): Optional[FetchResultDoc]

  def findByStatusCode(statusCode: Integer): java.util.List[FetchResultDoc]

  def findByHostAndStatusCode(host: String, statusCode: Integer): java.util.List[FetchResultDoc]

  @Aggregation(pipeline = Array(
    "{ $match: { host: { $ne: null } } }",
    "{ $group: { _id: \"$host\" } }"
  ))
  def findUniqueHost(): java.util.List[String]


  @Aggregation(pipeline = Array(
    "{ $match: { host: { $ne: null }, seed: ?0 } }",
    "{ $group: { _id: \"$host\" } }"
  ))
  def findUniqueHostBySeed(seed: String): java.util.List[String]
}
