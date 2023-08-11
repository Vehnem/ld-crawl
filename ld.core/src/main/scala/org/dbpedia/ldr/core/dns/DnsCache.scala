package org.dbpedia.ldr.core.dns

trait DnsCache {

  def resolve(host: String) : DnsResult
}
