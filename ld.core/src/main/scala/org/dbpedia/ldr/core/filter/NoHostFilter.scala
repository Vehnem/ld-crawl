package org.dbpedia.ldr.core.filter

class NoHostFilter extends HostFilter {

  override def tryHost(host: String): HostFilterResult = {
    HostFilterResult(accepted = true, error = None)
  }
}
