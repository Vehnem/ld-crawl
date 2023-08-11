package org.dbpedia.ldr.core.filter

import org.dbpedia.ldr.core.iri.IRI

trait HostFilter {

  def tryHost(host: String): HostFilterResult

  def tryIRI(iri: IRI): HostFilterResult = {
    tryHost(iri.toURI.getHost)
  }
}
