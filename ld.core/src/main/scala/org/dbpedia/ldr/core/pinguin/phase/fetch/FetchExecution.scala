package org.dbpedia.ldr.core.pinguin.phase.fetch

import org.dbpedia.ldr.core.pinguin.phase.PhaseData.{FetchData, IRIData}
import org.dbpedia.ldr.core.pinguin.phase.{PhaseData, PhaseExecution}

class FetchExecution(phaseData: IRIData, config: FetchConfig) extends PhaseExecution(phaseData, config) {

  override def execute(input: PhaseData): FetchData = {
    FetchData(input.getIri,config.httpClient.request(input.getIri))
  }
}
