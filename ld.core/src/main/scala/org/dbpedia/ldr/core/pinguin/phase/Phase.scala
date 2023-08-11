package org.dbpedia.ldr.core.pinguin.phase

import PhaseData.{FetchData, IDResData, ParseData}

sealed abstract class Phase(val label: String, val dataClazz: Class[_])
object Phase {
  final case object Fetch extends Phase(label = "fetch", dataClazz = classOf[FetchData])
  final case object Parse extends Phase(label = "parse", dataClazz = classOf[ParseData])
  final case object IDRes extends Phase(label = "id_resolution", dataClazz = classOf[IDResData])
}