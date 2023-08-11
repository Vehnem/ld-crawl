package org.dbpedia.ld.crawl.worker.tmp

import org.dbpedia.ldr.core.iri.IRI
import org.slf4j.LoggerFactory

import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable

class InMemoryIriRegistry(balancer: WorkBalancer) extends IriRegistry {

  private val log = LoggerFactory.getLogger(classOf[IriRegistry])

  private val registry = new ConcurrentHashMap[String,mutable.TreeSet[IRI]]()

  def register(iri: IRI): Unit = {
    log.trace(s"REGISTER $iri")
    val host = iri.toURI.getHost
    balancer.addHost(host)
    if(registry.containsKey(host)){
      registry.get(host).add(iri)
    } else {
      val set = new mutable.TreeSet[IRI]()
      set.add(iri)
      registry.put(host,set)
    }
  }

  def getIRIs(host: String): List[IRI] = {
    log.trace(s"GET IRIS FOR $host")
    registry.getOrDefault(host,new mutable.TreeSet[IRI]()).toList
  }
}

object InMemoryIriRegistry {

  val get = new InMemoryIriRegistry(InMemoryWorkBalancer.get)
}
