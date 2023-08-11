package org.dbpedia.ld.crawl.worker.tmp

import org.slf4j.LoggerFactory

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters.asScalaIteratorConverter

class InMemoryWorkBalancer extends WorkBalancer {

  private val log = LoggerFactory.getLogger(classOf[WorkBalancer])

  private val hosts = new ConcurrentHashMap[String, Long]()
  private val blocked = new ConcurrentHashMap[String, Long]()

  override def addHost(host: String): Unit = synchronized {
    log.trace(s"ADD HOST $host")
    hosts.put(host, System.currentTimeMillis())
  }

  override def removeHost(host: String): Unit = synchronized {
    hosts.remove(host)
  }

  override def numberOfHosts(): Integer = {
    hosts.size()
  }

  override def nextHost(): Option[String] = synchronized {
    val option = {
      hosts.keySet().iterator().asScala.find({
        !blocked.keySet.contains(_)
      }) match {
        case Some(host) =>
          // TODO +1 is difficult
          blocked.put(host, System.currentTimeMillis() + 1)
          Some(host)
        case None =>
          None
      }
    }
    option
  }

  override def hostDone(host: String): Unit = synchronized {
    if (hosts.get(host) < blocked.get(host)) {
      hosts.remove(host)
    }
    blocked.remove(host)
  }
}

object InMemoryWorkBalancer {
  val get = new InMemoryWorkBalancer
}
