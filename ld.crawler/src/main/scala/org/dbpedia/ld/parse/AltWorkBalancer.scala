package org.dbpedia.ld.parse

import org.dbpedia.ld.fetch.AltBatchJob
import org.redisson.api.{RBlockingQueue, RedissonClient}
import org.springframework.stereotype.Service

import java.util

@Service
class AltWorkBalancer(
  redissonClient: RedissonClient
) {

  private val queue: RBlockingQueue[AltBatchJob] = redissonClient.getBlockingQueue[AltBatchJob]("altparsejobqueue")
  queue.delete()

  def remaining: Int = queue.size()

  def getNextAltBatchJob: AltBatchJob = {
    queue.take()
  }

  def addAltBatchJob(batchJob: AltBatchJob): Unit = {
    queue.add(batchJob)
  }

  def addAltBatchJobs(batchJobs: util.Collection[AltBatchJob]): Unit = {
    queue.addAll(batchJobs)
  }
}