package org.dbpedia.ldr.core.iri

import org.dbpedia.ldr.core.util.HashUtil
import org.scalatest.funsuite.AnyFunSuite

import java.net.URI


class IRITest extends AnyFunSuite {

  test("iri parse") {
    //    val str = "http://plus.goo.ne.jp/rnkredir/redir.php?from=rss&to=http://search.goo.ne.jp/web.jsp?from=ranking_web&PT=ranking_web&MT=%25C5%25EC%25B5%25FE%25A5%25C7%25A5%25A3%25A5%25BA%25A5%25CB%25A1%25BC%25A5%25E9%25A5%25F3%25A5%25C9"
    //    val str = "http://plus.goo.ne.jp/rnkredir/redir.php?from=rss&to=http://search.goo.ne.jp/web.jsp?from=ranking_web&PT=ranking_web&MT=%C5%EC%B5%FE%A5%C7%A5%A3%A5%BA%A5%CB%A1%BC%A5%E9%A5%F3%A5%C9"
    //    val str = "http://af.dbpedia.org/property/etnieseGroep1%25"
    val str = "http://af.dbpedia.org/property/etnieseGroep1%25%F0%9F%98%81"

    val iriList = List(
      "http://af.dbpedia.org/property/etnieseGroep1%25%F0%9F%98%81",
      "http://ayurveda-foryou.com/affiliate/affiliate.html",
      "http://ayurveda-foryou.com/../affiliate/affiliate.html",
      "http://ayurveda-foryou.com/../../affiliate/affiliate.html",
      "http://ayurveda-foryou.com/.././affiliate/affiliate.html",
      "http://ayurveda-foryou.com/./../affiliate/affiliate.html",
      "http://ayurveda-foryou.com/./../affiliate/",
      "http://ayurveda-foryou.com/./../affiliate",
      "http://ayurveda-foryou.com/affiliate/.",
      "http://ayurveda-foryou.com/affiliate/..",
      "http://ayurveda-foryou.com",
      "http://ayurveda-foryou.com/affiliate/foo/../affiliate.html",
    )

    iriList.foreach({
      iri =>
        println()
        println(iri)
        println(parseIri(iri))
      //        val sb = new StringBuilder()
      //        sb.append(iri.scheme)
      //        sb.append("/")
      //        sb.append(iri.authority)
      //        sb.append("/")
      //        sb.append(HashUtil.hexSha256sum(iri.toString))
      //        println(sb.toString())
    })

  }

  def parseIri(iriStr: String): IRI = {
    val uri = new URI(iriStr)
    //    println(List(uri.getScheme,uri.getAuthority,uri.getPath,uri.getQuery,uri.getFragment).filter(_ != null).mkString("|"))
    IRI(uri, normalize = true)
  }
}
