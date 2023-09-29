package org.dbpedia.ldr.core

import org.redisson.api.RedissonClient

class RedisUrlQueue(redisson: RedissonClient) {

  private val knownIris = redisson.getSet[String](RedisCollections.KNOWN_IRIS)
  private val hashQueue = redisson.getQueue[String](RedisCollections.HASH_QUEUE)
  private val setCache = redisson.getSetCache[String]("some")
  private val mapCache = redisson.getMapCache[String,String]("some")


  // add to knownIris and queue if not exists
  def add(iri: String): Unit = {


  }

  def take(): Unit = {
//    mapCache.putIfAbsent()
  }
}
