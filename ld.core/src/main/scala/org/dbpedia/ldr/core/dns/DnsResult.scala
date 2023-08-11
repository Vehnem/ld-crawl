package org.dbpedia.ldr.core.dns

import org.dbpedia.ldr.core.SimplifiedException

case class DnsResult(host: String, aRecords: List[String], error: Option[SimplifiedException])