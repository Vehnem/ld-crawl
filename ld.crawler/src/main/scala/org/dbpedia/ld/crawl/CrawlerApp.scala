//package org.dbpedia.ld.crawl
//
//import org.dbpedia.ld.crawl.cli.Main
//import org.dbpedia.ld.crawl.config.CrawlerConfig
//import org.dbpedia.ld.crawl.dns.{DnsCache, RedisDnsCache}
//import org.dbpedia.ld.crawl.worker.tmp.RedisHostBalancer
//import org.dbpedia.ldr.core.data.{PostgresDataStore, PostgresDataStoreConfig}
//import org.dbpedia.ldr.core.metadata.{FetchResultCollection, ParseResultCollection, IriCollection}
//import org.redisson.api.RedissonClient
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.autoconfigure.SpringBootApplication
//import org.springframework.boot.{CommandLineRunner, ExitCodeGenerator, SpringApplication}
//import org.springframework.context.annotation.{Bean, Import}
//import org.springframework.data.mongodb.MongoDatabaseFactory
//import org.springframework.data.mongodb.core.convert.{DefaultDbRefResolver, MappingMongoConverter}
//import org.springframework.data.mongodb.core.mapping.MongoMappingContext
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
//import org.springframework.scheduling.annotation.EnableScheduling
//import org.springframework.stereotype.Component
//import picocli.CommandLine
//import picocli.CommandLine.IFactory
//
//@EnableScheduling
//@SpringBootApplication
//@Import(Array(classOf[PostgresDataStore],classOf[PostgresDataStoreConfig]))
//@EnableMongoRepositories(
//  basePackageClasses = Array(classOf[FetchResultCollection],classOf[IriCollection], classOf[ParseResultCollection]))
//class CrawlerApp {
//
//  @Component
//  class CLIRunner(
//  ) extends CommandLineRunner with ExitCodeGenerator {
//
//    @Autowired
//    var iFactory: IFactory = _
//
//    private var exitCode = 0
//
//    override def getExitCode: Int = exitCode
//
//    override def run(args: String*): Unit = {
//
//      exitCode =  new CommandLine(new Main, iFactory).execute(args: _*)
//    }
//  }
//
//  @Bean
//  def getDnsCache(redissonClient: RedissonClient): DnsCache = {
//    new RedisDnsCache(redissonClient)
//  }
//
//  @Bean
//  def getRedisHostBalancer(redissonClient: RedissonClient, crawlerConfig: CrawlerConfig): RedisHostBalancer = {
//    new RedisHostBalancer(redissonClient, crawlerConfig)
//  }
//
//  @Bean
//  def mongoConverter(
//    mongoFactory: MongoDatabaseFactory,
//    mongoMappingContext: MongoMappingContext
//  ): MappingMongoConverter = {
//    val dbRefResolver = new DefaultDbRefResolver(mongoFactory);
//    val mongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
//    //this is my customization
//    mongoConverter.setMapKeyDotReplacement("_")
//    mongoConverter.afterPropertiesSet()
//    mongoConverter
//  }
//}
//
//object CrawlerApp {
//
//  def main(args: Array[String]): Unit = {
//
//    if(args.isEmpty) {
//      System.exit(SpringApplication.exit(SpringApplication.run(classOf[CrawlerApp], List("-h"): _*)))
//    } else {
//      System.exit(SpringApplication.exit(SpringApplication.run(classOf[CrawlerApp], args: _*)))
//    }
//  }
//}
