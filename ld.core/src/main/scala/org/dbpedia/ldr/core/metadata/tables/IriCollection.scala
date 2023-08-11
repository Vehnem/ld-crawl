package org.dbpedia.ldr.core.metadata.old

import org.dbpedia.ldr.core.metadata.old.IriDoc
import org.springframework.data.mongodb.repository.{Aggregation, Meta, MongoRepository}

trait IriCollection extends MongoRepository[IriDoc, String] {

  def findByHostAndSeed(host: String, seed: String): java.util.List[IriDoc]

  def findByHost(domain: String): java.util.List[IriDoc]

  // TODO seed filter
  @Aggregation(pipeline = Array(
    "{ $match: { host: { $ne: null } } }",
    "{ $group: { _id: \"$host\" } }"
  ))
  @Meta(allowDiskUse = true)
  def findUniqueHost(): java.util.List[String]

  @Meta(allowDiskUse = true)
  @Aggregation(pipeline = Array(
    "{ $match: { host: { $ne: null }, seed: ?0 } }",
    "{ $group: { _id: \"$host\" } }"
  ))
  def findUniqueHostBySeed(seed: String): java.util.List[String]
}
