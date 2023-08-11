package org.dbpedia.ldr.core.metadata.old

import org.springframework.data.annotation.Id

import scala.annotation.meta.field
import scala.beans.BeanProperty


class ParseResultDoc(
  @(Id@field)
  @BeanProperty
  var location: String,
  @BeanProperty
  var locationHash: String,
  @BeanProperty
  var host: String,
  @BeanProperty
  var realIri: String,
  @BeanProperty
  var tripleCount: Long,
  @BeanProperty
  var typeInformation: String,
  @BeanProperty
  var errorClass: String,
  @BeanProperty
  var errorMessage: String
  // TODO timestamp
) {

  def this() =
    this(null, null, null, null, -1, null, null, null)

}
