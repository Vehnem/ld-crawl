package org.dbpedia.ldr.core.data

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

import scala.beans.BeanProperty

@Configuration
@ConfigurationProperties("ldr.data.postgres")
class PostgresDataStoreConfig {

  @BeanProperty
  var url: String = ""

  @BeanProperty
  var user: String = ""

  @BeanProperty
  var password: String = ""
}
