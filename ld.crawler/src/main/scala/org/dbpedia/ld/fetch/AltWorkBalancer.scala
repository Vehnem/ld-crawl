package org.dbpedia.ld.fetch

import org.redisson.api.{RBlockingQueue, RQueue, RedissonClient}
import org.springframework.stereotype.Service

@Service
class AltWorkBalancer(
  redissonClient: RedissonClient
) {

  private val queue: RBlockingQueue[AltBatchJob] = redissonClient.getBlockingQueue[AltBatchJob]("altjobqueue")
  queue.delete()

  def remaining: Int = queue.size()

  def getNextAltBatchJob: AltBatchJob = {
    queue.take()
  }

  def addAltBatchJob(batchJob: AltBatchJob): Unit = {
    queue.add(batchJob)
  }
}
