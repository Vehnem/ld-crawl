package org.dbpedia.ldr.core.pinguin.parse

import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.riot.RDFDataMgr
import org.dbpedia.ldr.core.SimplifiedException
import org.dbpedia.ldr.core.iri.IRI

import java.io.ByteArrayInputStream
import scala.util.{Failure, Success, Try}

class RDFParser extends Parser {

  override def parse(iri: IRI, data: Array[Byte], mimeType: String, charset: String): ParseResult = {
    tryParsing(data: Array[Byte], mimeType, charset) match {
      case Failure(ex) =>
        ParseResult(iri, None, null, null, Some(SimplifiedException(ex.getClass.getName,ex.getMessage)))
      case Success(model) =>
        ParseResult(iri, None, null, model, None )
    }
  }

  private def tryParsing(data: Array[Byte], mimeType: String, charset: String): Try[Model] = Try {
    val lang = FormatConversion.langByMimeType(mimeType)
    val model = ModelFactory.createDefaultModel()
    RDFDataMgr.read(model, new ByteArrayInputStream(data), lang)
    model
  }
}
