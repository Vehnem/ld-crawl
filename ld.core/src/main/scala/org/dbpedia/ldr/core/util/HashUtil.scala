package org.dbpedia.ldr.core.util

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object HashUtil {

  private def md5sum(bytes: Array[Byte]): Array[Byte] = {
    MessageDigest.getInstance("MD5").digest(bytes)
  }

  private def toHexString(bytes: Array[Byte]): String = {
    bytes.map("%02X".format(_)).mkString
  }

  def hexMd5sum(str: String): String = {
    toHexString(md5sum(str.getBytes(StandardCharsets.UTF_8)))
  }

  def hexMd5sum(bytes: Array[Byte]): String = {
    toHexString(md5sum(bytes))
  }

  def hexSha256sum(string: String): String = {
    val nUriBytes = string.getBytes(StandardCharsets.UTF_8)
    hexSha256sum(nUriBytes)
  }

  def hexSha256sum(bytes: Array[Byte]): String = {
    val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
    val hash: Array[Byte] = digest.digest(bytes)
    val hexString = new StringBuilder(2 * hash.length)

    hash.indices.foreach({
      i =>
        val hex = Integer.toHexString(0xff & hash(i))
        if (hex.length() == 1)
          hexString.append('0')
        hexString.append(hex)
    })
    hexString.toString()
  }
}
