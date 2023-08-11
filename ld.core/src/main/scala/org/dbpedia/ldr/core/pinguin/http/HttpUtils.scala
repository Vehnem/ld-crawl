package org.dbpedia.ldr.core.pinguin.http

import java.sql.Timestamp
import java.text.SimpleDateFormat

object HttpUtils {

  val format: SimpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")

  def parseRetryAfterHeaderToDelayMillis(current: Long, value: String): Long = {
    if(value.matches("\\d+")) {
      value.toLong*1000
    } else {
      val date = format.parse(value)
      val delay = current - date.getTime
      if (delay > 0 ) delay else 0
    }
    // TODO -1 if failure
  }
}
