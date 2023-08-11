package org.dbpedia.ldr.core.pinguin.parse

import org.slf4j.LoggerFactory
import ujson.Value

import java.util.concurrent.ConcurrentHashMap

class JsonldContextCache
object JsonldContextCache {

  private val log = LoggerFactory.getLogger(classOf[JsonldContextCache])

  private val cache = new ConcurrentHashMap[String, Value.Value]()

  def getCache: ConcurrentHashMap[String,Value.Value]  = {
    cache
  }

  def add(key: String, value: Value.Value): Unit = {
    log.info(s"ADDING $key")
    cache.put(key, value)
  }

  def get(key: String): Option[Value.Value] = {
    log.trace(s"GETTING $key")
    Option(cache.get(key))
  }
}
