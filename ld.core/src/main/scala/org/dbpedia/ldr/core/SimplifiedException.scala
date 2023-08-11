package org.dbpedia.ldr.core

case class SimplifiedException(className: String, msg: String) {

}

object SimplifiedException {

  def apply(e: Exception): SimplifiedException = {
    new SimplifiedException(e.getClass.getName, e.getMessage)
  }
}
