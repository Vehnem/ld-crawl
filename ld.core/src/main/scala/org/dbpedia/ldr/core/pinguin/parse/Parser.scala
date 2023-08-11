package org.dbpedia.ldr.core.pinguin.parse

import org.dbpedia.ldr.core.iri.IRI

trait Parser {

  def parse(iri: IRI, data: Array[Byte], mimeType: String, charset: String): ParseResult

}
