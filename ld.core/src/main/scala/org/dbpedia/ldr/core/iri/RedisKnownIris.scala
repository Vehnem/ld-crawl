package org.dbpedia.ldr.core.iri

import org.dbpedia.ldr.core.db.redis.RedisConfig
import org.redisson.api.RedissonClient

class RedisKnownIris(redissonClient: RedissonClient, redisConfig: RedisConfig) extends KnownIris {

  private val knownIris = redissonClient.getSet[String](redisConfig.KNOWN_IRIS)

  override def isKnown(iri: IRI): Boolean = {
    val contained = knownIris.contains(iri.toString)
    if (contained)
      true
    else
      knownIris.add(iri.toString)
      false
  }
}
