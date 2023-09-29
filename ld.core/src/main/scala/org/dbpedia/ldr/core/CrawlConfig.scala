package org.dbpedia.ldr.core

import java.util.concurrent.TimeUnit

class CrawlConfig {

  val header = "application/n-triples;q=1, text/turtle;q=0.9, application/n-quads;q=0.9, application/ld+json;q=0.8, application/rdf+xml;q=0.7, application/trig;q=0.7, */*;q=0.5"

  val requestDelay = "100"

  val maxRetryAfterDuration = "10000"

  val requestTimeout = "10000"

  val maxPayloadSize = 1e7

  object stopCondition {

    val numberOfExceptions = 50

    // Todo IOException, ConnectTimeoutException, HttpConnectTimeoutException
    val exceptions = List() // TODO
  }
}
