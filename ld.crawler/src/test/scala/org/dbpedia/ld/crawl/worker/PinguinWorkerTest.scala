package org.dbpedia.ld.crawl.worker

import ch.qos.logback.classic.{Level, Logger}
import org.dbpedia.ld.crawl.worker.tmp.{InMemoryIriRegistry, InMemoryWorkBalancer, IriRegistry, WorkBalancer}
import org.dbpedia.ldr.core.iri.IRI
import org.dbpedia.ldr.core.pinguin.http.{HttpClientConfig, HttpClientJava}
import org.dbpedia.ldr.core.pinguin.phase.fetch.FetchConfig
import org.dbpedia.ldr.core.pinguin.phase.{Phase, PhaseExecutionFactory}
import org.scalatest.funsuite.AnyFunSuite
import org.slf4j.LoggerFactory

class PinguinWorkerTest extends AnyFunSuite {

  private val iriRegistryLog = LoggerFactory.getLogger(classOf[IriRegistry]).asInstanceOf[Logger]
  iriRegistryLog.setLevel(Level.TRACE)
  private val workBalancerLog = LoggerFactory.getLogger(classOf[WorkBalancer]).asInstanceOf[Logger]
  workBalancerLog.setLevel(Level.TRACE)

  private val balancer: WorkBalancer = InMemoryWorkBalancer.get
  private val iriRegistry: IriRegistry = InMemoryIriRegistry.get

  test("pinguin worker test") {

    iriRegistry.register(IRI.apply("http://dbpedia.org/resource/Leipzig", normalize = true))
    iriRegistry.register(IRI.apply("http://dbpedia.org/resource/Eisenach", normalize = true))
    iriRegistry.register(IRI.apply("http://schema.org", normalize = true))
    iriRegistry.register(IRI.apply("https://www.imdb.com/name/nm0000206/", normalize = true))
    iriRegistry.register(IRI.apply("https://www.imdb.com/name/nm0000207/", normalize = true))

    PhaseExecutionFactory.setFetchConfig(new FetchConfig(new HttpClientJava(new HttpClientConfig())))

    val worker = new PinguinWorker(WorkerConfig(Phase.Fetch,true,InMemoryWorkBalancer.get,InMemoryIriRegistry.get))
    val t = new Thread(worker)
    t.start()

    while (balancer.numberOfHosts() > 0) {
      Thread.sleep(200)
    }
  }

  test("iri comparison") {
    val iri1 = IRI.apply("http://dbpedia.org/resource/Leipzig", normalize = true)
    val iri2 = IRI.apply("http://dbpedia.org/resource/Eisenach", normalize = true)

    println(iri1.compare(iri2))
  }
}
