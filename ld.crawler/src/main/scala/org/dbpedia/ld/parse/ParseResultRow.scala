package org.dbpedia.ld.parse

case class ParseResultRow(iri: String, timestamp: Long, data: Array[Byte]) {
  def to3Tuple: (String, Long, Array[Byte]) = (iri, timestamp, data)
}
