package org.dbpedia.ldr.core.pinguin.phase

import org.dbpedia.ldr.core.pinguin.phase.PhaseData.{FetchData, IRIData}
import org.dbpedia.ldr.core.pinguin.phase.fetch.{FetchConfig, FetchExecution}
import org.dbpedia.ldr.core.pinguin.phase.parse.{ParseConfig, ParseExecution}

object PhaseExecutionFactory {

  private var fetchConfig: FetchConfig = _
  def setFetchConfig(fetchConfig: FetchConfig): Unit = {
    this.fetchConfig = fetchConfig
  }

  private var parseConfig: ParseConfig = _

  def create(phase: Phase, phaseData: PhaseData): PhaseExecution = {
    phase match {
      case Phase.Fetch =>
        new FetchExecution(phaseData.asInstanceOf[IRIData], fetchConfig)
      case Phase.Parse =>
        new ParseExecution(phaseData.asInstanceOf[FetchData], parseConfig)
      case Phase.IDRes =>
        null
    }
  }
}
