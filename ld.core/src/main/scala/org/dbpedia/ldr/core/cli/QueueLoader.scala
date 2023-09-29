package org.dbpedia.ldr.core.cli

import org.redisson.Redisson
import org.redisson.client.codec.StringCodec
import org.redisson.config.Config

import scala.io.Source
import scala.jdk.CollectionConverters._

object QueueLoader {

  def main(args: Array[String]): Unit = {

    val config = new Config();
    config.useSingleServer().setAddress("redis://127.0.0.1:6379");

    val redisson = Redisson.create(config);

    val q = redisson.getQueue[List[String]]("test:queue",StringCodec.INSTANCE)


    val source = Source.fromFile(args(0))

    source.getLines().map({
      line =>
        line.split("\t").toList
    }).grouped(100).foreach({
      group =>
        q.addAll(group.asJava)
    })

    source.close()
  }
}
