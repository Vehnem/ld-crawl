package org.dbpedia.ldr.core.pinguin.phase.parse

import org.apache.jena.rdf.model.ModelFactory
import org.dbpedia.ldr.core.SimplifiedException
import org.dbpedia.ldr.core.pinguin.parse.ParseResult
import org.dbpedia.ldr.core.pinguin.phase.Phase.Parse
import org.dbpedia.ldr.core.pinguin.phase.PhaseData.{FetchData, ParseData}
import org.dbpedia.ldr.core.pinguin.phase.{PhaseData, PhaseExecution}

class ParseExecution(phaseData: FetchData, config: ParseConfig) extends PhaseExecution(phaseData, config) {

  override def execute(input: PhaseData): PhaseData = {
    val fetchData = input.asInstanceOf[FetchData]
    if (fetchData.getData.body.length == 0) {
      ParseData(input.getIri, ParseResult(input.getIri, None, null, null, Some(SimplifiedException("org.foo", "empty body"))))
    } else {
      PhaseData.getDummyData(fetchData.iri, Parse)
    }
  }
}
