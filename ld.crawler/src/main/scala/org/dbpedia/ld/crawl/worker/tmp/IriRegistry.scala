package org.dbpedia.ld.crawl.worker.tmp

import org.dbpedia.ldr.core.iri.IRI

trait IriRegistry {

  def register(iri: IRI): Unit

  def getIRIs(host: String): List[IRI]

}
