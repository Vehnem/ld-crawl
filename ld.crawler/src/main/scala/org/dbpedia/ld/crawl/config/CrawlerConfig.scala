package org.dbpedia.ld.crawl.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

import java.beans.BeanProperty

@Configuration
@ConfigurationProperties(prefix = "ldr.crawl")
class CrawlerConfig {

  @BeanProperty
  var minRetryAfter: Int = 200 //ms

  @BeanProperty
  var maxRetryAfter: Int = 10000 //ms

  @BeanProperty
  var maxRedirects: Int = 10

  @BeanProperty
  var timeout: Int = 10

  @BeanProperty
  var acceptHeader: String =
    "application/n-triples;q=1," +
      "text/turtle;q=0.9," +
      "application/n-quads;q=0.9," +
      "application/ld+json;q=0.8," +
      "application/rdf+xml;q=0.7," +
      "application/trig;q=0.7," +
      "*/*;q=0.5"

  @BeanProperty
  var writeBehindLimit: Int = 100

  @BeanProperty
  var serverId: String = "default_server_id"

  /** @deprecated */
  @BeanProperty
  var outputDirectory: String = "./fetchsimple/"

  /** @deprecated */
  @BeanProperty
  var resolverThreads: Int = 100
}
