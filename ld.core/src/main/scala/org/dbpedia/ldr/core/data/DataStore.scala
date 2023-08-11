package org.dbpedia.ldr.core.data

import java.io.{File, InputStream}

trait DataStore {

  def save(uri: String, bytes: Array[Byte], extension: String = ".fetched"): File

  def find(uri: String, extension: String = ".fetched"): Option[InputStream]

  def getFile(uri: String, extension: String = ".fetched"): File
}
