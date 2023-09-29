package org.dbpedia.ld.fetch

import org.dbpedia.ldr.core.balancer.{RedisHostBalancer, WorkBalancer}
import org.dbpedia.ldr.core.db.postgres.{PostgresDataStore, PostgresDataStoreConfig}
import org.dbpedia.ldr.core.dns.{DnsCache, DnsResolver, PublicDnsResolver}
import org.dbpedia.ldr.core.filter.{HostFilter, IpBasedHostFilter, NoHostFilter}
import org.dbpedia.ldr.core.metadata.old.{FetchResultCollection, HostCollection, IriCollection, ParseResultCollection}
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.boot.{CommandLineRunner, ExitCodeGenerator, SpringApplication}
import org.springframework.boot.autoconfigure.SpringBootApplication
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
  value = Array(classOf[PostgresDataStore],classOf[PostgresDataStoreConfig]))
@EnableMongoRepositories(
  basePackageClasses = Array(classOf[IriCollection],classOf[FetchResultCollection],classOf[HostCollection]))
@EnableScheduling
class Boot {

  @Value("$.ld.fetch.dropRegistry:true}")
  var dropRegistry: Boolean = true

  @Bean
  def getWorkBalancer(redissonClient: RedissonClient, fetchConfig: FetchConfig): WorkBalancer = {
    new RedisHostBalancer(redissonClient,fetchConfig.serverId, "hosts", dropRegistry)
  }

  @Bean
  def getDnsResolver: DnsResolver = {
    new PublicDnsResolver
  }

  @Bean
  def getDnsCache(dnsResolver: DnsResolver, hostCollection: HostCollection): DnsCache = {
    new MongoDbDnsCache(dnsResolver, hostCollection)
  }

  @Bean
  def getHostFilter(dnsCache: DnsCache): HostFilter = {
    new IpBasedHostFilter(dnsCache)
  }

  @Component
  class Scheduler(
    services: FetchServices
  ) {
    @Scheduled(fixedDelay = 60000)
    def monitorThreads(): Unit = {
       services.threadStatus()
    }
    @Scheduled(fixedDelay = 300000)
    def flushWriteBehind(): Unit = {
      services.writeBehindDataService.flush()
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

      exitCode =  new CommandLine(cli, iFactory).execute(args: _*)
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
