package org.dbpedia.ld.crawl.fetch

import org.apache.commons.lang3.concurrent.TimedSemaphore
import org.scalatest.funsuite.AnyFunSuite

import java.util.concurrent.TimeUnit

class SemaphoreTests extends AnyFunSuite {

  val ts = new TimedSemaphore(500, TimeUnit.MILLISECONDS, 5)


  test("timed semaphore") {

    (0 until 10).foreach({
      _ =>
        new Thread(new Runnable {
          override def run(): Unit = {
            while (true) {
              while(ts.tryAcquire()) {
                println(System.currentTimeMillis() + " " + Thread.currentThread().getName + " acquired permit")
              }
            }
          }
        }).start()
    })

    Thread.sleep(60000)
  }
}
