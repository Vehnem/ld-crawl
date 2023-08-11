package org.dbpedia.ldr.core.filter

import org.dbpedia.ldr.core.SimplifiedException

import java.util

case class HostFilterResult(accepted: Boolean, error: Option[SimplifiedException]) {

  def toMap: java.util.Map[String,Object] = {
    val map = new util.TreeMap[String,Object]()
    map
  }
}
