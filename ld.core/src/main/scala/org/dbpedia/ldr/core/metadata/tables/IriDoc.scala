package org.dbpedia.ldr.core.metadata.old

import org.dbpedia.ldr.core.iri.IRI
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

import java.util
import scala.annotation.meta.field
import scala.beans.BeanProperty

@Document
class IriDoc(

  @(Id@field)
  @BeanProperty
  var iri: String,

  @BeanProperty
  var hash: String,

  @(Indexed@field)(sparse = true)
  @BeanProperty
  var location: String,

  @BeanProperty
  var locationHash: String,

  @(Indexed@field)(sparse = true)
  @BeanProperty
  var host: String,

  @(Indexed@field)
  @BeanProperty
  var seed: java.util.List[String],

  @BeanProperty
  var error: String,

  @BeanProperty
  var errorMessage: String,
) {

  def toIRI: IRI = {
    IRI.apply(iri,normalize = true)
  }

  def toMap: util.TreeMap[String, Object] = {
    val map = new util.TreeMap[String,Object]()
    map.put("_id",iri)
    if(null != hash) map.put("hashsum",hash)
    if(null != location) map.put("location",location)
    if(null != locationHash) map.put("locationHashsum",locationHash)
    if(null != host) map.put("host",host)
    map.put("seed",seed)
    if(null != error) map.put("error",error)
    if(null != errorMessage) map.put("errorMessage",errorMessage)
    map
  }

  def this() =
    this(null, null, null, null, null, null, null, null)

}