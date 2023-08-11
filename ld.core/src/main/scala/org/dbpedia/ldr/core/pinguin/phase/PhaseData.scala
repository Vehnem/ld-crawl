package org.dbpedia.ldr.core.pinguin.phase

import org.apache.jena.rdf.model.ModelFactory
import org.dbpedia.ldr.core.iri.IRI
import org.dbpedia.ldr.core.pinguin.http.HttpExchangeResult
import org.dbpedia.ldr.core.pinguin.parse.ParseResult

sealed trait PhaseData {
  def getIri: IRI

  def getData: Object

  def dependsOn: Phase
}

object PhaseData {
  case class IRIData(iri: IRI) extends PhaseData {
    override def getIri: IRI = iri

    override def getData: IRI = iri

    override def dependsOn: Phase = null
  }

  case class FetchData(iri: IRI, data: HttpExchangeResult) extends PhaseData {
    override def getIri: IRI = iri

    override def getData: HttpExchangeResult = data

    override def dependsOn: Phase = null
  }

  case class ParseData(iri: IRI, data: ParseResult) extends PhaseData {
    override def getIri: IRI = iri

    override def getData: ParseResult = data

    override def dependsOn: Phase = Phase.Fetch
  }

  case class IDResData(iri: IRI, data: IRI) extends PhaseData {
    override def getIri: IRI = iri

    override def getData: IRI = data

    override def dependsOn: Phase = Phase.Parse
  }

  def getDummyData(iri: IRI, phase: Phase): PhaseData = {
    phase match {
      case Phase.Fetch =>
        FetchData(iri, HttpExchangeResult(iri, 0, List(), Array[Byte](), List(), None, 0, 0))
      case Phase.Parse =>
        ParseData(iri, ParseResult(iri, None, null, ModelFactory.createDefaultModel(),None))
    }
  }

  def getDummyData(iriString: String, phase: Phase): PhaseData = {
    getDummyData(IRI.apply(iriString, normalize = true), phase)
  }
}

