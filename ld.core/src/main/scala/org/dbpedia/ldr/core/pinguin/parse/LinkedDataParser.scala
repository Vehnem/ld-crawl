package org.dbpedia.ldr.core.pinguin.parse

import org.dbpedia.ldr.core.iri.IRI
import org.dbpedia.ldr.core.pinguin.http.{HttpClientConfig, HttpClientJava}
import org.dbpedia.ldr.core.pinguin.parse.idres.Id2EntityResolver
import org.dbpedia.ldr.core.util.HashUtil

class LinkedDataParser {

  val htmlJsonldParser = new JsonldParser()
  val rdfParser = new RDFParser
  val id2EntityResolver = new Id2EntityResolver(new HttpClientJava(new HttpClientConfig()), this)

  def parseAndFindMapping(iri: IRI, mimetype: String, charset: String, data: Array[Byte], followedIRIs: List[IRI]): ParseResult = {
    val parseResult = parse(iri, mimetype, charset, data)
    if (parseResult.error.isDefined || parseResult.model == null) {
      parseResult
    } else {
      id2EntityResolver.findMapping(iri, followedIRIs, parseResult.model, HashUtil.hexMd5sum(data)) match {
        case Some(cdiri) =>
          parseResult.copy(cdiri = Some(cdiri))
        case None =>
          parseResult
      }
    }
  }

  def parse(iri: IRI, mimetype: String, charset: String, data: Array[Byte]): ParseResult = {
    if (FormatConversion.isJsonldType(mimetype) || !FormatConversion.langByMimeType.keySet.contains(mimetype)) {
      // parse JSONLD
      htmlJsonldParser.parse(iri, data, mimetype, charset).copy(typeInformation = s"Jsonld: $mimetype")
    } else {
      // parse RDF
      rdfParser.parse(iri, data, mimetype, charset).copy(typeInformation = s"RDF: $mimetype")
    }
  }
}
