package org.dbpedia.ld.crawl.store

trait Store[T] {

//  def configure(config: StoreConfig): Unit

  def save(doc: T): Boolean

  def find(id: String): T

  def delete: Boolean
}
