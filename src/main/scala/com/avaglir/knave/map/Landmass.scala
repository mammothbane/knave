package com.avaglir.knave.map

import com.avaglir.knave.util._

case class Landmass(sizeClass: IslandClass, tiles: Set[Vector2[Double]], name: Option[String], adjective: Option[String]) {
  def area = tiles.size
}

object Landmass extends Persist with Random {
  val ISLAND_THRESHOLD = 0.02
  val ISLE_THRESHOLD = 0.007

  lazy val (continents, islands, isles, atolls) = {
    val isl = Islands.all.toSet
    val continents = Set(Landmass(IslandClass.Continent, isl.maxBy { _.size }, None, None))
    val totalSize = Islands.all.map { _.size }.sum - continents.head.area

    val wIsl = isl - continents.head.tiles

    val islands = wIsl.filter { _.size.toFloat / totalSize > ISLAND_THRESHOLD }.map { tiles => Landmass(IslandClass.Island, tiles, None, None) }

    val isles = wIsl.filter { elem =>
      val sz = elem.size.toFloat / totalSize
      sz > ISLE_THRESHOLD && sz <= ISLAND_THRESHOLD
    }.map { tiles => Landmass(IslandClass.Isle, tiles, None, None) }

    val atolls = wIsl.filter { elem =>
      val sz = elem.size.toFloat / totalSize
      sz <= ISLE_THRESHOLD
    }.map { tiles => Landmass(IslandClass.Atoll, tiles, None, None) }

    (continents, islands, isles, atolls)
  }

  lazy val all = continents ++ islands ++ isles ++ atolls

  override def persist(): Map[Symbol, String] = Map(
    'random -> seed.toString
  )

  override def restore(v: Map[Symbol, String]): Unit = {
    setSeed(v('random).toDouble)
  }

  override def key: Symbol = 'landmass
}
