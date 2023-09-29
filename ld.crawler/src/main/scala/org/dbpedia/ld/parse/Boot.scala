package org.dbpedia.ld.parse

import org.dbpedia.ldr.core.balancer.{RedisHostBalancer, WorkBalancer}
import org.dbpedia.ldr.core.db.postgres.{PostgresDataStore, PostgresDataStoreConfig}
import org.dbpedia.ldr.core.metadata.old.{FetchResultCollection, HostCollection, IriCollection, ParseResultCollection}
import org.redisson.api.RedissonClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.{CommandLineRunner, ExitCodeGenerator, SpringApplication}
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.convert.{DefaultDbRefResolver, MappingMongoConverter}
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.scheduling.annotation.{EnableScheduling, Scheduled}
import org.springframework.stereotype.Component
import picocli.CommandLine
import picocli.CommandLine.IFactory

@SpringBootApplication
@Import(
  value = Array(classOf[PostgresDataStore], classOf[PostgresDataStoreConfig]))
@EnableMongoRepositories(
  basePackageClasses = Array(classOf[FetchResultCollection], classOf[HostCollection], classOf[ParseResultCollection], classOf[IriCollection]))
@EnableScheduling
class Boot {

  @Bean
  def getWorkBalancer(redissonClient: RedissonClient): WorkBalancer = {
    new RedisHostBalancer(redissonClient, serverId = "default", "hosts_parse", dropRegistry = true)
  }

  @Component
  class Scheduler(
    services: ParseServices
  )  {

    @Scheduled(fixedDelay = 30000)
    def monitorThreads(): Unit = {
      services.threadStatus()
    }
  }

  @Component
  class CLIRunner(
    iFactory: IFactory,
    cli: CLI
  ) extends CommandLineRunner with ExitCodeGenerator {

    private var exitCode = 0

    override def getExitCode: Int = exitCode

    override def run(args: String*): Unit = {

      exitCode = new CommandLine(cli, iFactory).execute(args: _*)
    }
  }

  @Bean
  def mongoConverter(
    mongoFactory: MongoDatabaseFactory,
    mongoMappingContext: MongoMappingContext
  ): MappingMongoConverter = {
    val dbRefResolver = new DefaultDbRefResolver(mongoFactory);
    val mongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
    mongoConverter.setMapKeyDotReplacement("_")
    mongoConverter.afterPropertiesSet()
    mongoConverter
  }
}

object Boot {

  def main(args: Array[String]): Unit = {
    System.exit(SpringApplication.exit(SpringApplication.run(classOf[Boot], args: _*)))

  }
}
