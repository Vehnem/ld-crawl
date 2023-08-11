package org.dbpedia.ldr.core.dns

case class ARecord(
  name: String,
  rdata: List[String]
)
