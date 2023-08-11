package org.dbpedia.ldr.core.balancer

trait WorkBalancer {

  def addHost(host: String): Unit

  // TODO needed as synchronized
  def removeHost(host: String): Unit

  def numberOfHosts(): Integer

  def nextHost(): Option[String]

  def hostDone(host: String): Unit
}
