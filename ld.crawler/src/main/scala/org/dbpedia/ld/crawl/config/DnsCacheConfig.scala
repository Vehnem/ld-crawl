package org.dbpedia.ld.crawl.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

import scala.beans.BeanProperty

@Configuration
@ConfigurationProperties(prefix = "ldr.crawl.dns")
class DnsCacheConfig {

  @BeanProperty
  var ttl: Integer = 86400 // in Seconds

  @BeanProperty
  var requestsPerSecond: Integer = 20
}
