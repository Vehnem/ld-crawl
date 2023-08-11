package org.dbpedia.ld.crawl.worker.phase

import org.dbpedia.ldr.core.pinguin.phase.PhaseData.{IRIData, ParseData}
import org.dbpedia.ldr.core.iri.IRI
import org.dbpedia.ldr.core.pinguin.phase.{Phase, PhaseData, PhaseExecutionFactory}
import org.scalatest.funsuite.AnyFunSuite

class PhaseFactoryTest extends AnyFunSuite {

  test("main") {
    testFactory(Phase.Parse, PhaseData.getDummyData("http://dbpedia.org/resource/Leipzig",Phase.Fetch))
    testFactory(Phase.Fetch, IRIData(IRI.apply("http://dbpedia.org/resource/Leipzig", normalize = true)))
  }

  def testFactory(phase: Phase, inData: PhaseData): Unit = {
    val exec = PhaseExecutionFactory.create(phase,inData)
    val outData = exec.call()
    println(outData)
  }
}
