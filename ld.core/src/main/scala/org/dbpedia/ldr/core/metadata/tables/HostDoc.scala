package org.dbpedia.ldr.core.metadata.old

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import scala.annotation.meta.field
import scala.beans.BeanProperty

@Document
class HostDoc(

  @(Id@field)
  @BeanProperty
  var host: String,

  @BeanProperty
  var ip: java.util.List[String],

  @BeanProperty
  var lastCheck: Long,

  @BeanProperty
  var error: String,

  @BeanProperty
  var errorMessage: String
) {

  def this() =
    this(null, null, -1, null, null)

}
