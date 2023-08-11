package org.dbpedia.ld.crawl.cli

import org.springframework.stereotype.Component
import picocli.CommandLine.Command

import java.util.concurrent.Callable

@Component
@Command(name = "crawl", mixinStandardHelpOptions = true)
class Crawl extends Callable[Integer] {
  override def call(): Integer = {
    0 // Success
  }
}
