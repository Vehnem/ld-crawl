package org.dbpedia.ld.parse

import org.springframework.stereotype.Component
import picocli.CommandLine.{Command, Option}

import java.util
import java.util.concurrent.Callable
import scala.collection.JavaConverters.iterableAsScalaIterableConverter

@Component
@Command(name = "main", mixinStandardHelpOptions = true, subcommands = Array(classOf[DumpTriples], classOf[ParseResults]))
class CLI(

) extends Callable[Integer] {

  override def call(): Integer = {
    0 // SUCCESS

  }
}

