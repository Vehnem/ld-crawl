package org.dbpedia.ld.crawl.worker

import org.dbpedia.ld.crawl.worker.tmp.{IriRegistry, WorkBalancer}
import org.dbpedia.ldr.core.pinguin.phase.Phase

case class WorkerConfig(
  target: Phase,
  rideover: Boolean,
  workBalancer: WorkBalancer,
  iriRegistry: IriRegistry
)
