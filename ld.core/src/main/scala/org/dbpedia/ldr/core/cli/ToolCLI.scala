package org.dbpedia.ldr.core.cli

import com.monovore.decline.{Command, CommandApp, Opts}

import java.nio.file.Path
import cats.implicits.*
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.RDFDataMgr

object ToolCLI  {

  val file = Opts.argument[Path](metavar = "file")

  val some = Opts.options[String](long = "somelong" , metavar = "some", help = "somehelp")

  val subcommand = Opts.subcommand("somesub",help = "some sub help") {
    (file, some).tupled
  }

  val command = Command(
    name = "tool-cli",
    header = "..."
  ) {
    (file, some, subcommand).tupled
  }

  def main(args: Array[String]): Unit = command.parse(args, sys.env) match {
    case Left(help) if help.errors.isEmpty =>
      // help was requested by the user, i.e.: `--help`
      println(help)
      sys.exit(0)

    case Left(help) =>
      // user needs help due to bad/missing arguments
      System.err.println(help)
      sys.exit(1)

    case Right(parsedValue) =>
//      println(subcommand.orFalse.)
    parsedValue
    // Your program goes here!

  }

//  val model = ModelFactory.createDefaultModel()
//  model.read("","")
//
//  RDFDataMgr.read()

//  val subcommand
}
