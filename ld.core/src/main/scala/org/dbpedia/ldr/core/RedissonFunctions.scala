package org.dbpedia.ldr.core

import org.redisson.Redisson
import org.redisson.api.{BatchOptions, FunctionMode, FunctionResult, RFunction, RedissonClient}
import org.redisson.api.RScript.{Mode, ReturnType}
import org.redisson.client.codec.StringCodec
import org.redisson.client.protocol.RedisCommands
import org.redisson.config.Config

import java.util.Collections
import scala.jdk.CollectionConverters.*

class RedissonFunctions(client: RedissonClient) {

  private val GROUPS_INUSE = "groups:inuse"
  private val GROUPS_AWAIT = "groups:await"
  private val GROUPS_QUEUE = "groups:queue"

  private val IRIS_KNOWN = "iris:known"
  private val IRIS_QUEUE = "iris:queue"

  private val ADD_GROUP_FUN_NAME = "add_group"
  private val NEXT_GROUP_FUN_NAME = "next_group"

  private val ADD_IRI_FUN_NAME = "add_iri"
  private val NEXT_IRI_FUN_NAME = "next_iri"

  private val rFun = client.getFunction(StringCodec.INSTANCE)

  private val addGroupFunDef =
    """function(keys, args)
      |  local occupied_set = keys[1]
      |  local available_set = keys[2]
      |  local queue_list = keys[3]
      |  local group = args[1]
      |
      |  local is_occupied = redis.call('HEXISTS', occupied_set, group)
      |  local is_available = redis.call('HEXISTS', available_set, group)
      |
      |  if is_occupied == 0 and is_available == 0 then
      |    local time = redis.call('TIME')[1]
      |    redis.call('HSET', available_set, group, time)
      |    redis.call('RPUSH', queue_list, group)
      |    return true
      |  else
      |   return false
      |  end
      |end""".stripMargin


  private val nextGroupFunDef =
    """function(keys, args)
      |  local occupied_set = keys[1]
      |  local available_set = keys[2]
      |  local queue_list = keys[3]
      |
      |  local group = redis.call('LPOP', queue_list)
      |  if group then
      |     local time = redis.call('TIME')[1]
      |     redis.call('HSET', occupied_set, group, time)
      |     redis.call('HDEL', available_set, group)
      |  end
      |
      |  return group
      |end""".stripMargin

  private val addIriFunDef =
    s"""function(keys, args)
       |  local occupied_set = keys[1]
       |  local available_set = keys[2]
       |  local queue_list = keys[3]
       |  local queue = keys[4]
       |  local known_set = keys[5]
       |
       |  local group = args[1]
       |  local iri = args[2]
       |
       |  local is_known = redis.call('SISMEMBER', known_set, iri)
       |  if is_known == 1 then
       |    return false
       |  else
       |    redis.call('SADD', known_set, iri)
       |
       |    local is_occupied = redis.call('HEXISTS', occupied_set, group)
       |    local is_available = redis.call('HEXISTS', available_set, group)
       |
       |    if is_occupied == 0 and is_available == 0 then
       |      local time = redis.call('TIME')[1]
       |      redis.call('HSET', available_set, group, time)
       |      redis.call('RPUSH', queue_list, group)
       |    end
       |
       |    redis.call('RPUSH', queue, iri)
       |    return true
       |  end
       |end""".stripMargin

  def nextIriFunDef =
    s"""function(keys, args)
       |  local iri_queue = keys[1]
       |  local inuse_set = keys[2]
       |  local group = args[1]
       |
       |  local iri = redis.call('LPOP', iri_queue)
       |  if iri then
       |    redis.call('HDEL', inuse_set, group)
       |    return iri
       |  else
       |    return iri
       |  end
       |end""".stripMargin

  rFun.loadAndReplace(
    "lib",
    s"redis.register_function('$NEXT_GROUP_FUN_NAME', $nextGroupFunDef);" +
      s"redis.register_function('$ADD_GROUP_FUN_NAME', $addGroupFunDef);" +
      s"redis.register_function('$ADD_IRI_FUN_NAME', $addIriFunDef);" +
      s"redis.register_function('$NEXT_IRI_FUN_NAME', $nextIriFunDef);"
  )

  def addGroup(group: String): Long = {
    rFun.call[Long](
      FunctionMode.READ,
      ADD_GROUP_FUN_NAME,
      FunctionResult.VALUE,
      List(GROUPS_INUSE, GROUPS_AWAIT, GROUPS_QUEUE).asJava,
      group
    )
  }

  def nextGroup(): Option[String] = {
    val group: String =
      rFun.call[String](
        FunctionMode.READ,
        NEXT_GROUP_FUN_NAME,
        FunctionResult.VALUE,
        List(GROUPS_INUSE, GROUPS_AWAIT, GROUPS_QUEUE).asJava,
        ""
      )
    Option(group)
  }

  // TODO return boolean
  def addIri(iri: String, group: String): Unit = {
    rFun.call(
      FunctionMode.READ,
      ADD_IRI_FUN_NAME,
      FunctionResult.VALUE,
      List(GROUPS_INUSE, GROUPS_AWAIT, GROUPS_QUEUE, IRIS_QUEUE + ":" + group, IRIS_KNOWN).asJava,
      group, iri
    )
  }

  def addIris(list: List[(String, String)]): Unit = {
    val batch = client.createBatch(BatchOptions.defaults())

    list.foreach({
      (group, iri) =>
      batch.getFunction(StringCodec.INSTANCE).callAsync(
        FunctionMode.READ,
        ADD_IRI_FUN_NAME,
        FunctionResult.VALUE,
        List(GROUPS_INUSE, GROUPS_AWAIT, GROUPS_QUEUE, IRIS_QUEUE + ":" + group, IRIS_KNOWN).asJava,
        group, iri
      )
    })

    val res = batch.execute()
    res.getResponses
  }

  def nextIri(group: String): Option[String] = {
    val iri = rFun.call[String](
      FunctionMode.READ,
      NEXT_IRI_FUN_NAME,
      FunctionResult.VALUE,
      List(IRIS_QUEUE + ":" + group, GROUPS_INUSE).asJava,
      group
    )
    Option(iri)
  }

  def releaseGroup(group: String): Long = {
    // TODO
    1L
  }
}
