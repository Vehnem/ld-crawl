package org.dbpedia.ld.crawl.scheduled

import org.dbpedia.ld.crawl.store.DataStoreService
import org.dbpedia.ld.crawl.worker.tmp.WorkerExecution
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduledTasks(
  workerExecution: WorkerExecution,
  dataStoreService: DataStoreService
) {

  @Scheduled(fixedRate = 30000)
  def checkPoolStatus(): Unit = {
    workerExecution.checkStatus()
  }

  @Scheduled(fixedRate = 30000)
  def flushDB(): Unit = {
    dataStoreService.writeToDb()
  }
}
