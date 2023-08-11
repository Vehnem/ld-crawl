package org.dbpedia.ldr.core.dns

import org.dbpedia.ldr.core.SimplifiedException
import org.xbill.DNS.{DClass, ExtendedResolver, Message, Name, Record, Section, Type}

import java.time.Duration
import java.util.concurrent.Executors
import scala.collection.JavaConverters.iterableAsScalaIterableConverter

class PublicDnsResolver extends DnsResolver {

  val dnsServers = List(
    "8.8.8.8",
    "8.8.4.4",
    "1.1.1.1",
    "1.0.0.1",
    "208.67.222.222",
    "208.67.220.220",
    "9.9.9.9",
    "149.112.112.112",
    "185.228.168.168",
    "185.228.169.168",
    "209.244.0.3",
    "209.244.0.4",
    "64.6.64.6",
    "64.6.65.6",
    "199.85.126.10",
    "199.85.127.10",
    "77.88.8.8",
    "77.88.8.1",
    "176.103.130.130",
    "176.103.130.131",
    "84.200.69.80",
    "84.200.70.40",
    "156.154.70.1",
    "156.154.71.1",
    "37.235.1.174",
    //    "37.235.1.177",
    //    "91.239.100.100",
    //    "89.233.43.71",
    //    "8.26.56.26",
    "8.20.247.20",
    //    "76.76.19.19",
    "76.223.122.150",
    "74.82.42.42",
    "195.46.39.39",
    "195.46.39.40",
    "216.146.35.35",
    "216.146.36.36",
    "9.9.9.11",
    "149.112.112.11"
  )

  private val rs = new ExtendedResolver(
    dnsServers.toArray
  )

  rs.setLoadBalance(true)
  rs.setTimeout(Duration.ofSeconds(10))
  rs.setRetries(3)

  private val executors = Executors.newFixedThreadPool(10)

  def resolve(host: String): DnsResult = {
    try {
      val name = Name.fromString(host + ".")
      val record = Record.newRecord(name, Type.A, DClass.IN)
      val message = Message.newQuery(record)
      rs.sendAsync(message, executors).toCompletableFuture.handle[DnsResult](
        (answer: Message, ex: Throwable) => {
          val hostName = answer.getQuestion.getName.toString.dropRight(1)
          if (ex == null) {
            val aRecords = {
              answer.getSection(Section.ANSWER).asScala.flatMap({
                record =>
                  extractARecord(hostName, record)
              })
            }
            DnsResult(hostName, aRecords.toList, None)
          } else {
            val question = answer.getQuestion
            DnsResult(
              hostName,
              List(),
              Some(SimplifiedException(ex.getClass.getName, ex.getMessage))
            )
          }
        }
      ).get()
    } catch {
      case ex: Exception =>
        DnsResult(
          host,
          List(),
          Some(SimplifiedException(ex.getClass.getName, ex.getMessage))
        )
    }
  }

  def extractARecord(host: String, record: Record): Option[String] = {
    if (record.getType == Type.A) {
      Some(record.rdataToString())
    } else {
      None
    }
  }
}
