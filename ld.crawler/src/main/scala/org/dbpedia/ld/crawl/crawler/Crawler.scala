package org.dbpedia.ld.crawl.crawler

import jakarta.annotation.PreDestroy
import org.springframework.boot.{CommandLineRunner, SpringApplication}
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Component


@SpringBootApplication
class Crawler {

  @Component
  class SomeClass {

    @PreDestroy
    def destroy(): Unit = {
      println("Callback triggered - @PreDestroy")
    }
  }

  @Component
  class Initializer extends CommandLineRunner {
    override def run(args: String*): Unit = {
      while (true) {
        Thread.sleep(100)
      }
    }
  }

}

object Crawler {

  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[Crawler],args: _*)

  }
}