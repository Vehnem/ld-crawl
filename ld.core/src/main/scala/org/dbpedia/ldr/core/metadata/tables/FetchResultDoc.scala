package org.dbpedia.ldr.core.metadata.old

import org.bson.Document
import org.dbpedia.ldr.core.metadata.old.FetchResultDoc
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed

import java.util
import scala.annotation.meta.field
import scala.beans.BeanProperty

//@Document
class FetchResultDoc(

  @(Id@field)
  @BeanProperty var location: String,

  @BeanProperty
  var locationHash: String,

  @(Indexed@field)
  @BeanProperty var seed: java.util.List[String],

  @(Indexed@field)
  @BeanProperty
  var host: String,

  @(Indexed@field)(sparse = true)
  @BeanProperty
  var statusCode: Int,

  @BeanProperty
  var dataSize: Long,

  @BeanProperty
  var headers: util.List[util.Map[String, util.List[String]]],

  @BeanProperty
  var followedIri: util.List[util.Map[String, String]],

  @BeanProperty
  var contentType: String,

  @BeanProperty
  var charset: String,

  @BeanProperty
  var error: String,

  @BeanProperty
  var errorMessage: String,

  @BeanProperty
  var timeStamp: Long,

  @BeanProperty
  var duration: Long,
) {
  def this() =
    this(null, null, null, null, -1, -1, null, null, null, null, null, null, -1, -1)

}

object FetchResultDoc {

  def fromDocument(doc: Document): FetchResultDoc = {

    val fetchDoc = new FetchResultDoc()

    //    //    var location: String,
    //    fetchDoc.setLocation(doc.getString("_id"))
    //    //    var locationHash: String,
    //    fetchDoc.setLocationHash(doc.getString("locationHash"))
    //    //    var seed: java.util.List[String],
    //    fetchDoc.setSeed(doc.getList("seed",classOf[String]))
    //    //    var host: String,
    //    fetchDoc.setHost(doc.getString("host"))
    //    //    var statusCode: Int,
    //    fetchDoc.setStatusCode(doc.getInteger("statusCode",-1))
    //    //    var dataSize: Long,
    //    fetchDoc.setDataSize(doc.getLong("dataSize"))
    //    //    var headers: util.List[util.Map[String, util.List[String]]],
    ////    fetchDoc.setHeaders("headers")
    ////    extractHeaders(doc)
    //    //    var followedIri: util.List[util.Map[String,String]],
    //    fetchDoc.setFollowedIri(doc.getList[util.Map[String,String]]("followedIri", classOf[util.Map[String,String]]))
    //    //    var contentType: String,
    //    fetchDoc.setContentType(doc.getString("contentType"))
    //    //    var charset: String,
    //    fetchDoc.setCharset(doc.getString( "charset"))
    //    //    var error: String,
    //    fetchDoc.setError(doc.getString("error"))
    //    //    var errorMessage: String,
    //    fetchDoc.setErrorMessage(doc.getString("errorMessage"))
    //    //    var timeStamp: Long,
    //    fetchDoc.setTimeStamp(doc.getLong("timeStamp"))
    //    //    var duration: Long
    //    fetchDoc.setDuration(doc.getLong("duration"))
    //
    fetchDoc
  }

  private def extractHeadersFromDocument(): Unit = {
    // TODO
  }
}
