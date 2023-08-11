package org.dbpedia.ldr.core.pinguin.parse

import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.dbpedia.ldr.core.SimplifiedException
import org.dbpedia.ldr.core.iri.IRI
import org.dbpedia.ldr.core.pinguin.http.{HttpClientConfig, HttpClientJava}
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import ujson.Value

import java.io.{ByteArrayInputStream, StringReader}
import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.util.{Failure, Success, Try}

class JsonldParser extends Parser {

  private val log = LoggerFactory.getLogger(classOf[JsonldParser])

  override def parse(iri: IRI, data: Array[Byte], mimeType: String, charset: String): ParseResult = {
    val possibleModel =
      if (FormatConversion.isJsonldType(mimeType)) {
        tryParseJsonld(iri, data, charset)
      } else {
        tryParseHtmlJsonld(iri, data, charset)
      }
    possibleModel match {
      case Failure(ex) =>
        ParseResult(iri, None, null, null, Some(SimplifiedException(ex.getClass.getName, ex.getMessage)))
      case Success(model) =>
        ParseResult(iri, None, null, model, None)
    }
  }

  private def tryParseHtmlJsonld(iri: IRI, data: Array[Byte], charset: String): Try[Model] = Try {
    val bis = new ByteArrayInputStream(data)
    val document = Jsoup.parse(bis, stringToUpperCase(charset), iri.toString)
    val models = {
      document.select("script[type=application/ld+json]").asScala.toList.map({
        jsonldElement =>
          val jsonldDString = jsonldElement.html() // TODO html() vs data()
          val jsonldData: Value.Value = ujson.read(jsonldDString)
          parseJsonldData(iri, jsonldData, charset)
      })
    }
    val model = ModelFactory.createDefaultModel()
    models.foreach(model.add)
    model
  }

  def tryParseJsonld(iri: IRI, data: Array[Byte], charset: String): Try[Model] = Try {
    val jsonldData = ujson.read(new ByteArrayInputStream(data))
    parseJsonldData(iri, jsonldData, charset)
  }

  def parseJsonldData(iri: IRI, jsonldData: Value.Value, charset: String): Model = {
    val model = ModelFactory.createDefaultModel()
    if (jsonldData.arrOpt.isDefined) {
      jsonldData.arr.toList.foreach({
        nestedJsonldData =>
          try {
            model.add(parseJsonldData(iri, nestedJsonldData, charset))
          } catch {
            case exception: Exception =>
            // TODO exception.printStackTrace()
          }
      })
      model
    } else {
      try {
        parseJsonldDataObj(iri, jsonldData, charset)
      } catch {
        case exception: Exception =>
        // TODO exception.printStackTrace()
          model
      }
    }
  }

  def parseJsonldDataObj(iri: IRI, jsonldData: Value.Value, charset: String): Model = {
    if (jsonldData("@context").value.isInstanceOf[String]) {
      Value.Selector.StringSelector("@context").update(jsonldData, handleContext(jsonldData("@context")))
    }

    val model = ModelFactory.createDefaultModel()
    try {
      RDFDataMgr.read(model, new StringReader(jsonldData.toString()), iri.toString, Lang.JSONLD)
    } catch {
      case ex: Exception =>
    }
    model
  }

  private def handleContext(contextValue: Value.Value): Value.Value = {
    try {
      val iri = IRI.apply(contextValue.str, normalize = true)
      resolveContext(iri) match {
        case Some(value) =>
          value
        case None =>
          contextValue
      }
    } catch {
      case ex: Exception =>
        contextValue
    }
  }

  private def resolveContext(iri: IRI): Option[Value.Value] = {
    JsonldContextCache.get(iri.toString) match {
      case Some(value) =>
        Some(value)
      case None =>
        val client = new HttpClientJava(new HttpClientConfig()) // TODO Config maybe use singleton
        val httpExchangeResult = client.request(iri)
        // TODO un-nest
        if (httpExchangeResult.error.isDefined) {
          None
        } else {
          if (FormatConversion.isJsonldType(httpExchangeResult.mimeType.orNull)) {
            val value = ujson.read(new ByteArrayInputStream(httpExchangeResult.body))("@context")
            JsonldContextCache.add(iri.toString, value)
            Some(value)
          } else {
            None
          }
        }
    }
  }


  private def stringToUpperCase(string: String): String = {
    if (null != string) {
      string.toUpperCase()
    } else {
      null
    }
  }

  //  def parseEmbedded(inputStream: InputStream, baseUri: URI): Try[Model] = Try {
  //    val document = Jsoup.parse(inputStream, null, "")
  //    val m = ModelFactory.createDefaultModel()
  //    document.select("script[type=application/ld+json]").iterator().asScala.foreach({
  //      ele =>
  //        val jsonld = ele.html
  //        val data: Value.Value = ujson.read(jsonld)
  //        //        data("@context") = schema_org_context
  //        //        Value.Selector.StringSelector("@context").update()
  //        try {
  //          data("@id") = data("url")
  //        } catch {
  //          case _: Throwable =>
  //            println("could not perform 'data(@id) = data(url)'")
  //        }
  //        val dataWithBase =
  //          data.render().replaceFirst("\\{\"type", "{\"@base\": \"" + baseUri + "\", \"type")
  //        //println(dataWithBase)
  //        RDFDataMgr.read(m, new StringReader(dataWithBase), baseUri.toString, Format.RDF_JSONLD().jenaLang)
  //    })
  //    m
  //  }

}
