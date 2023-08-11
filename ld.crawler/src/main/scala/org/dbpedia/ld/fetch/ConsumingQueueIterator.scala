package org.dbpedia.ld.fetch

import java.util

class ConsumingQueueIterator[T](queue: util.Queue[T]) extends Iterator[T] {
  var current: Option[T] = None
  override def hasNext: Boolean = {
    null != queue.peek()
  }

  override def next(): T = {
    current = Some(queue.peek())
    queue.poll()
  }
}
