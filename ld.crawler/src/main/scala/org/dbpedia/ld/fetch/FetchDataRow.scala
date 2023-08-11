package org.dbpedia.ld.fetch

case class FetchDataRow(iri: String, timestamp: Long, data: Array[Byte]) {
  def to3Tuple: (String, Long, Array[Byte]) = (iri, timestamp, data)
}
