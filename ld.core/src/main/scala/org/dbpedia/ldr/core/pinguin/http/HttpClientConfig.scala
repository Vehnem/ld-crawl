package org.dbpedia.ldr.core.pinguin.http

import org.dbpedia.ldr.core.filter.{HostFilter, NoHostFilter}

class HttpClientConfig(

  val timeout : Int = 10,
  val acceptHeader : String = "application/n-triples;q=1,text/turtle;q=0.9,application/n-quads;q=0.9,application/ld+json;q=0.8,application/rdf+xml;q=0.7,application/trig;q=0.7,*/*;q=0.5",
  val userAgentHeader: String = "DBpedia-LD-Client",
  val maxRedirects : Int = 10,
  val minRequestDelay : Long = 0, //ms
  val maxRequestDelay : Long = 10000, //ms
  val maxResourceSize : Long = 10000000, // bytes

  val hostFilter: HostFilter = new NoHostFilter,

  val mimeTypePreference: List[String] = List(
    "application/n-triples",
    "text/turtle",
    "application/n-quads",
    "application/ld+json",
    "application/rdf+xml",
    "application/trig"
  )
)
