package org.dbpedia.ldr.core.pinguin.phase

import PhaseData.{FetchData, IRIData}
import org.dbpedia.ldr.core.iri.IRI

import java.util.concurrent.Callable

abstract class PhaseExecution(phaseData: PhaseData, config: PhaseConfig) extends Callable[PhaseData] {

  override def call(): PhaseData = {
    val input = {
      if (phaseData.getData == null) {
        callPreviousPhase()
      } else phaseData
    }
    execute(input)
  }

  protected def execute(input: PhaseData): PhaseData

  private def callPreviousPhase(): PhaseData = {
    phaseData match {
      case PhaseData.FetchData(iri, data) =>
        PhaseExecutionFactory.create(phaseData.dependsOn, IRIData(iri)).call()
      case PhaseData.ParseData(iri, data) =>
        PhaseExecutionFactory.create(phaseData.dependsOn, FetchData(iri, null)).call()
    }
  }
}
