package org.dbpedia.ldr.core.monitor

class ThroughPutWatch {

  private var startT: Long = -1
  private var currentC: Long = 0
  private var currentT: Long = -1

  def start(): Unit = {
    startT = System.currentTimeMillis()
  }

  def increase(value: Long): Unit = {
    if(startT < 0) {
      start()
    } else {
      currentT = System.currentTimeMillis()
    }
    currentC += value
  }

  def increaseAndGetTPS(value: Long): Float = {
    increase(value)
    //TODO corner case
    get()
  }

  def get(): Float = {
    println(s"current: $currentC elapsed: ${currentT-startT}")
    currentC/((currentT-startT)/1000.floatValue())
  }

  def counter(): Long = {
    currentC
  }
}
