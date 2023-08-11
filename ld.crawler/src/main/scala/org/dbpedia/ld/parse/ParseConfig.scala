package org.dbpedia.ld.parse

import org.springframework.stereotype.Component

import scala.beans.BeanProperty

@Component
class ParseConfig {

  @BeanProperty
  var skipExisting: Boolean = true

}
