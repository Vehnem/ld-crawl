package org.dbpedia.ldr.core.pinguin.parse.idres

import org.apache.jena.rdf.model.{Model, RDFNode}
import org.dbpedia.ldr.core.iri.IRI
import org.dbpedia.ldr.core.pinguin.http.HttpClientJava
import org.dbpedia.ldr.core.pinguin.parse.LinkedDataParser
import org.dbpedia.ldr.core.util.HashUtil
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.collection.mutable

class Id2EntityResolver(httpClientJava: HttpClientJava, linkedDataParser: LinkedDataParser) {

  private val log = LoggerFactory.getLogger(classOf[Id2EntityResolver])

  def findMapping(iri: IRI, followedIRIs: List[IRI], model: Model, hash: String): Option[IRI] = {
    val candidateIRIs: Set[String] = getCandidatesFromModel(model)
    val requestIRIs: List[String] = List(iri.toString) ++ followedIRIs.map(_.toString)

    requestIRIs.find(candidateIRIs.contains) match {
      case Some(cIriString) =>
        Some(IRI.apply(cIriString, normalize = true))
      case None =>
        findInDifferentSchema(iri, candidateIRIs, requestIRIs, hash)
    }
  }

  private def findInDifferentSchema(iri: IRI, candidateIRIs: Set[String], requestIRIs: List[String], hash: String): Option[IRI] = {
    log.trace(s"try to find by different schema ${iri.toString}")
    requestIRIs.map(swapHttpS).find(candidateIRIs.contains) match {
      case Some(candidateCIRIString) =>
        if (testCandidate(candidateCIRIString, hash)) {
          Some(IRI.apply(candidateCIRIString, normalize = true))
        } else {
          None
        }
      case None => None
    }
  }

  private def testCandidate(iriString: String, hash: String): Boolean = {
    log.trace(s"test candidate $iriString")
    val data = httpClientJava.request(IRI.apply(iriString, normalize = true)).body
    null != data && HashUtil.hexMd5sum(data) == hash
  }

  private def swapHttpS(iriString: String): String = {
    if (iriString.startsWith("http:"))
      "https" + iriString.substring(4, iriString.length)
    else if (iriString.startsWith("https:"))
      "http" + iriString.substring(5, iriString.length)
    else iriString
  }

  /**
   * objects are included because of the constructs used by schema.org model
   * @param model
   * @return
   */
  private def getCandidatesFromModel(model: Model): Set[String] = {
    val set = new mutable.HashSet[String]()
    model.listStatements().asScala.foreach({
      stmt =>
        val sbjIRI = node2Iri(stmt.getSubject)
        if (sbjIRI.isDefined) set.add(sbjIRI.get.toString)
        val objIRI = node2Iri(stmt.getObject)
        if (objIRI.isDefined) set.add(objIRI.get.toString)
    })
    set.toSet
  }

  private def node2Iri(rdfNode: RDFNode): Option[IRI] = {
    if (rdfNode.asNode().isURI) {
      try {
        Some(IRI.apply(rdfNode.asResource().getURI, normalize = true).locationIRI)
      } catch {
        case ex: Exception =>
          None
      }
    } else {
      None
    }
  }
}
