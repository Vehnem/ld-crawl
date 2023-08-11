package org.dbpedia.ld.crawl.worker

import org.dbpedia.ld.crawl.worker.tmp.{IriRegistry, WorkBalancer}
import org.dbpedia.ldr.core.pinguin.phase.PhaseData.IRIData
import org.dbpedia.ldr.core.pinguin.phase.{Phase, PhaseExecutionFactory}

/*

 */
class PinguinWorker(conf: WorkerConfig) extends Runnable {

  var continue = true

  override def run(): Unit = {
    conf.target match {
      case Phase.Fetch =>
        request()
      case Phase.Parse =>
        parse()
      case Phase.IDRes =>
    }
  }

  private def request(): Unit = {
    val balancer: WorkBalancer = conf.workBalancer
    val iriRegistry: IriRegistry = conf.iriRegistry

    var currentHost: Option[String] = None
    while (continue) {
      // TODO this is not good
      while (currentHost.isEmpty) {
        currentHost = balancer.nextHost()
        Thread.sleep(10)
      }

      iriRegistry.getIRIs(currentHost.get).foreach({
        iri =>
          val exec = PhaseExecutionFactory.create(conf.target,IRIData(iri))
          val res = exec.call()
          println(res)
      })

      balancer.hostDone(currentHost.get)
      currentHost = None
    }
  }

  private def parse(): Unit = {

  }

}
