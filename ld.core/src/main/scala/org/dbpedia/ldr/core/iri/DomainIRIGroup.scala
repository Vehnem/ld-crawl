package org.dbpedia.ldr.core.iri

class DomainIRIGroup extends IRIGroup {

  override def getGroup(iri: IRI): String = {
    iri.authority
  }
}