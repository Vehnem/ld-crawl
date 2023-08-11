package org.dbpedia.ldr.core.pinguin.parse

import org.apache.jena.riot.Lang

object FormatConversion {

  val langByMimeType = Map(
    "application/n-triples" -> Lang.NTRIPLES,
    "text/turtle" -> Lang.TURTLE,
    "application/n-quads" -> Lang.NQUADS,
    "application/ld+json" -> Lang.JSONLD,
    "application/rdf+xml" -> Lang.RDFXML,
    "application/trig" -> Lang.TRIG
  )

  def isJsonldType(mimetype: String): Boolean = {
    if (null != mimetype) {
      (mimetype == "application/ld+json") || (mimetype == "application/json")
    } else {
      false
    }
  }
}
