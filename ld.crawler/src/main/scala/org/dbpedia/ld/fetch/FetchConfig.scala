package org.dbpedia.ld.fetch

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import scala.beans.BeanProperty

@Component
class FetchConfig(

) {

  @Value("$.ld.fetch.maxRetryAfter:10000}")
  var maxRetryAfter: Integer = 10000 //ms

  @Value("$.ld.fetch.minRetryAfter:100}")
  var minRetryAfter: Integer = 100

  @Value("$.ld.fetch.maxResourceSize:10000000}")
  var maxResourceSize: Integer = 10000000 // 10mb

  @Value("$.ld.fetch.minHostTime:60}")
  var minHostTime: Long = 60

  @Value("$.ld.fetch.avgResourceTime:60}")
  var avgResourceTime: Long = 2

  @Value("$.ld.fetch.hostExceptionLimit:50}")
  var hostExceptionLimit: Int = 50

  @BeanProperty
  var seed: String = null

  @Value("$.ld.fetch.serverId:default}")
  var serverId: String = "default"

  @BeanProperty
  var skipExisting: Boolean = false

  @BeanProperty
  var useHostQueryFile: Boolean = false

  @BeanProperty
  var retryStatus: Int = Integer.MIN_VALUE

}
