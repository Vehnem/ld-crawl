package org.dbpedia.ldr.core.balancer

import java.util.TreeMap
import scala.jdk.CollectionConverters._
import scala.collection.mutable

class ConsistentHash[T](numberOfReplicas: Int, nodes: Iterable[T]) {

  private val circle = new TreeMap[Int, T]()

  nodes.foreach(add)

  def add(node: T): Unit = {
    for (i <- 0 until numberOfReplicas) {
      val hash = (node.toString + i).hashCode
      println(hash)
      circle.put(hash, node)
    }
  }

  def remove(node: T): Unit = {
    for (i <- 0 until numberOfReplicas) {
      circle.remove((node.toString + i).hashCode)
    }
  }

  def get(key: Any): Option[T] = {
    if (circle.isEmpty) {
      return None
    }

    var hash = key.hashCode
    print(s"$hash ")
    if (!circle.containsKey(hash)) {
      val tailMap = circle.tailMap(hash)
      hash = if (tailMap.isEmpty) circle.firstKey else tailMap.firstKey
    }

    Option(circle.get(hash))
  }
}


