package org.dbpedia.ldr.core.metadata

import org.dbpedia.ldr.core.iri.IRI

case class CrawlDoc(
  iri: IRI,
  dataService: Any
) {

// lazy val data: Array[Byte]

// lazy val meta: Map[String,Object]
}