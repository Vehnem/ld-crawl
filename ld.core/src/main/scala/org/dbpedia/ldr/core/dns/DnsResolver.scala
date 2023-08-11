package org.dbpedia.ldr.core.dns

trait DnsResolver {

  def resolve(host: String): DnsResult

}
