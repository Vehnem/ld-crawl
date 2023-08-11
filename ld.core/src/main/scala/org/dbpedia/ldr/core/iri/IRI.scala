package org.dbpedia.ldr.core.iri

import org.dbpedia.ldr.core.util.HashUtil

import java.net.URI
import java.util.Locale
import scala.language.implicitConversions
import org.apache.jena.iri.{IRI => JenaIRI}
import org.apache.jena.irix.IRIx

case class IRI(var scheme: String, var authority: String, var path: String, var query: String, var fragment: String) extends Ordered[IRI] {

  private val DEFAULT_PORT_MAP = Map(
    "http" -> 80,
    "https" -> 443
  )

  def normalize(sortQuery: Boolean = false): IRI = {

    var t_scheme = scheme
    var t_authority = authority
    var t_path = path
    var t_query = query
    var t_fragment = fragment

    t_scheme = lowerCase(t_scheme)
    t_authority = lowerCase(t_authority)

    t_authority = normalizePercent(t_authority)
    t_path = normalizePercent(t_path)
    t_query = normalizePercent(t_query)
    t_fragment = normalizePercent(t_fragment)

    // Sort Query
    if (sortQuery) t_query = sortQueryParameter(t_query)

    // Path Segment Normalization

    if (t_path != null)
      t_path = removeDotSegment(t_path)
//      println("t_path: " + t_path)
    else if (t_path == null || t_path.isEmpty)
      t_path = "/"

    // Scheme-Based Normalization

    // remove trailing ":" no port
    if (t_authority != null && t_authority.endsWith(":"))
      t_authority = t_authority.substring(0, t_authority.length - 1)

    // remove default port
    DEFAULT_PORT_MAP.get(t_scheme).foreach({
      port =>
        val trail: String = ":" + port
        if (t_authority.endsWith(trail))
          t_authority = t_authority.substring(0, t_authority.length - trail.length)
    })


    // Protocol-Based Normalization
    // Not Implemented

    new IRI(t_scheme, t_authority, t_path, t_query, t_fragment)
  }

  def lowerCase(string: String): String = {
    string.toLowerCase(Locale.ROOT)
  }

  def normalizePercent(string: String): String = {
    if (string == null)
      string
    else {
      val idx = string.indexOf('%')
      if (idx < 0)
        string
      else {
        val len = string.length
        val sb = new StringBuilder()
        var i = 0
        while (i < len) {
          val char = string.charAt(i)
//          println(char + " " + string + " " +i)
          if (i+2 >= len || !charIsPctEncoded(char, string, i)) {
            i += 1
            sb.append(char)
          } else {
            val char1 = upperCaseASCII(string.charAt(i + 1))
            val char2 = upperCaseASCII(string.charAt(i + 2))
            i += 3

            val x = (charHexValue(char1) * 16 + charHexValue(char2)).toChar
            if (charisUnreserved(x)) {
              sb.append(x)
            } else {
              sb.append("%" + char1 + char2)
            }
          }
        }
        sb.toString()
      }
    }
  }

  def charIsPctEncoded(char: Char, string: String, i: Int): Boolean = {
    if (char != '%')
      false
    else {
      percentCheck(i, string.charAt(i + 1), string.charAt(i + 2))
    }
  }

  private val EOF = 0xFFFF

  def percentCheck(i: Int, c1: Char, c2: Char): Boolean = {
    if (c1 == EOF || c2 == EOF) {
      false // TODO Error?
    } else {
      if (isHexDigit(c1) && isHexDigit(c2)) { // todo changed from || to &&
        true
      } else {
        false // TODO Error?
      }
    }
  }

  def sortQueryParameter(querystring: String): String = {
    if (querystring == null || querystring.isEmpty)
      querystring
    else {
      querystring.split("&").sorted.mkString("&")
    }
  }

  def charisUnreserved(char: Char): Boolean = {
    if (isAlpha(char) || isDigit(char)) {
      true
    } else {
      char match {
        case '-' => true
        case '.' => true
        case '_' => true
        case '~' => true
        case _ => false
      }
    }
  }

  def isAlpha(char: Char): Boolean = {
    range(char, 'a', 'z') || range(char, 'A', 'Z')
  }

  def isDigit(char: Char): Boolean = {
    range(char, '0', '9')
  }

  def isHexDigit(char: Char): Boolean = {
    range(char, '0', '9') || range(char, 'A', 'F') || range(char, 'a', 'f')
  }

  def range(char: Int, start: Int, finish: Int): Boolean = {
    char >= start && char <= finish
  }

  def charHexValue(char: Char): Int = {
    if (range(char, '0', '9')) char - '0'
    else if (range(char, 'A', 'F')) char - 'A' + 10
    else if (range(char, 'a', 'f')) char - 'a' + 10
    else -1
  }

  def upperCaseASCII(char: Char): Char = {
    if (char >= 'a' && char <= 'z')
      (char + ('A' - 'a')).toChar
    else
      char
  }

  def removeDotSegment(path: String): String = {
    // TODO
    if (path == null || path.isEmpty) {
      "/"
    } else if (path == "/") {
      "/"
    } else {
      val segments = path.split('/')
//      println("segments: "+segments.map("'"+_+"'").mkString("|"))
      val N = segments.length

      val initialSlash: Boolean = segments.head.isEmpty
      var trailingSlash: Boolean = false

      if (N > 1) {
        if (segments(N - 1) == "." || segments(N - 1) == "..")
          trailingSlash = true
        else if (path.endsWith("/"))
          trailingSlash = true
      }

      var j = 0
      while (j < N) {
        val segment = segments(j)
        if (segment == ".") segments(j) = null
        if (segment == "..") {
//          println("path:  " + path)
          segments(j) = null
          if (j > 1) { // TODO check (was changed to > instead of >=)
            var k = j - 1
            while (k >= 0 && segments(k) != "") { // TODO check (was chagnged to ..&& k != "")
              if (segments(k) != null) {
                segments(k) = null
                k = 0
              }
              k -= 1
            }
          }
        }
        j += 1
      }
//      println("t_segments: "+segments.map("'"+_+"'").mkString("|"))
      if (trailingSlash)
//        println("yes trailing /")
        segments.filter(_ != null).mkString("/") + "/"
      else
//        println("no trailing /")
        segments.filter(_ != null).mkString("/")
    }
  }

  override def toString: String = {
    toURI.toString
  }

  private def _toString: String = {
    // TODO incomplete
    val sb = new StringBuilder()
    if (scheme != null) {
      sb.append(scheme)
      sb.append(':')
    }
    if (authority != null) {
      sb.append("//")
      sb.append(authority)
    }
    if (path != null) {
      sb.append(path)
    }
    if (query != null) {
      sb.append('?')
      sb.append(query)
    }
    if (fragment != null) {
      sb.append('#')
      sb.append(fragment)
    }
    sb.toString()
  }

  def toURI: URI = {
    new URI(scheme, authority, path, query, fragment)
  }

  def sha256sum: String = {
    HashUtil.hexSha256sum(this.toString)
  }

  def locationIRI: IRI = {
    new IRI(this.scheme, this.authority, this.path, this.query, null)
  }

  override def compare(that: IRI): Int = {
    this.toString.compare(that.toString)
  }

  lazy val host: String = toURI.getHost
}


object IRI {

  def apply(string: String, normalize: Boolean): IRI = {
    apply(new URI(string),normalize)
  }

  def apply(uri: URI, normalize: Boolean): IRI = {
//    println("path " + uri.getPath)
    val iri = IRI(uri.getScheme, uri.getAuthority, uri.getPath, encodeLastPercent(uri.getQuery), uri.getFragment)
    if (normalize)
      iri.normalize(true)
    else
      iri
  }

  private def encodeLastPercent(str: String): String = {
    if(str != null && str.endsWith("%"))
      str + "25"
    else str
  }
}
