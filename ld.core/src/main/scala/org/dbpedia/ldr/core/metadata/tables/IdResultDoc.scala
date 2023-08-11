package org.dbpedia.ldr.core.metadata.old

import org.springframework.data.annotation.Id

import scala.annotation.meta.field
import scala.beans.BeanProperty

class IdResultDoc(

  @(Id@field)
  @BeanProperty
  var location: String,

  @BeanProperty
  var consistentIri: String,
)
