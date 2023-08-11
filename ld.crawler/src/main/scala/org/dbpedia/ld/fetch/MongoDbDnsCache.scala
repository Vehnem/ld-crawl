package org.dbpedia.ld.fetch

import org.dbpedia.ldr.core.SimplifiedException
import org.dbpedia.ldr.core.dns.{DnsCache, DnsResolver, DnsResult}
import org.dbpedia.ldr.core.metadata.old.{HostCollection, HostDoc}

import scala.collection.JavaConverters.{collectionAsScalaIterableConverter, seqAsJavaListConverter}

class MongoDbDnsCache(
  dnsResolver: DnsResolver,
  hostCollection: HostCollection
) extends DnsCache {

  override def resolve(host: String): DnsResult = {
    val possibleHostDoc = hostCollection.findById(host)
    if(possibleHostDoc.isPresent) {
      val hostDoc = possibleHostDoc.get()
      var simpleError : Option[SimplifiedException] = None
      if(null != hostDoc.error) simpleError = Some(SimplifiedException(hostDoc.error,hostDoc.errorMessage))
      DnsResult(
        host,
        hostDoc.ip.asScala.toList,
        simpleError)
    } else {
      val dnsResult = dnsResolver.resolve(host)
      val hostDoc = new HostDoc()
      hostDoc.lastCheck = System.currentTimeMillis()
      hostDoc.host = host
      hostDoc.ip = dnsResult.aRecords.asJava
      if(dnsResult.error.isDefined) {
        hostDoc.error = dnsResult.error.get.className
        hostDoc.errorMessage = dnsResult.error.get.msg
      }
      hostCollection.save(hostDoc)
      dnsResult
    }
  }
}
