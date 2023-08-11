package org.dbpedia.ldr.core.pinguin.parse

import org.apache.jena.rdf.model.Model
import org.dbpedia.ldr.core.SimplifiedException
import org.dbpedia.ldr.core.iri.IRI

case class ParseResult(
  iri: IRI,
  cdiri: Option[IRI],
  typeInformation: String,
  model: Model,
  error: Option[SimplifiedException]
) {

  override def toString: String = {
    val sb = new StringBuilder
    sb.append("ParseResult(\n")
    sb.append(s"  iri = $iri\n")
    if(cdiri.isDefined) {
      sb.append(s"  cdiri = $cdiri\n")
    }
    if(null != model) {
      sb.append(s"  model.size = ${model.size()}\n")
    }
    if(error.isDefined) {
      sb.append(s"  error = ${error.get}\n")
    }
    sb.append(")")
    sb.toString()
  }

}
