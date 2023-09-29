package org.dbpedia.ldr.core.util

import java.util
import java.util.Map
import java.util.LinkedHashMap;

class FixedSizeCache[K,V](maxSize: Int) extends util.LinkedHashMap[K,V](maxSize+2,1F) {

  override def removeEldestEntry(eldest: util.Map.Entry[K, V]): Boolean = {
    size() > maxSize
  }
}

