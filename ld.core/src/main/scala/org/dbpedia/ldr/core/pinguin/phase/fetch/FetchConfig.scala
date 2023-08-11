package org.dbpedia.ldr.core.pinguin.phase.fetch

import org.dbpedia.ldr.core.pinguin.http.HttpClientJava
import org.dbpedia.ldr.core.pinguin.phase.PhaseConfig

case class FetchConfig(httpClient: HttpClientJava) extends PhaseConfig
