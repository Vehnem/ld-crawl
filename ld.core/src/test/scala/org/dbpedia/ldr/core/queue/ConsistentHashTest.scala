package org.dbpedia.ldr.core.queue

import org.dbpedia.ldr.core.balancer.ConsistentHash
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ConsistentHashTest extends AnyFunSuite{

  test("test") {
    val nodes = List("List1", "List2", "List3")
    val consistentHash = new ConsistentHash[String](1, nodes)

    val countMap = mutable.Map[String, Int]().withDefaultValue(0)
    for (i <- 0 until 10000) {
      val key = i.toString
      val listName = consistentHash.get(key).orNull
      countMap(listName) += 1
    }

    countMap.foreach { case (k, v) => println(s"$k: $v") }
  }
}