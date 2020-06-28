package com.avaglir.knave.map

import com.avaglir.knave.util._

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobalScope, JSName}

case class Landmass(
    sizeClass: IslandClass,
    tiles: Set[Vector2[Int]],
    name: Option[String],
    adjective: Option[String],
  ) {
  def area: Int = tiles.size

  lazy val center: Vector2[Int] = tiles.fold(Vector2.ZERO[Int])(_ + _) / tiles.size

  lazy val edge: Set[Vector2[Int]] = tiles.filter(tile => tile.adjacent.exists(!tiles.contains(_)))

  def print: String = sizeClass.withModifiers(adjective, name)
}

object Landmass extends Random {
  val ISLAND_THRESHOLD = 0.02
  val ISLE_THRESHOLD = 0.007

  def which(v: Vector2[Int]): Option[Landmass] =
    all.find {
      _.tiles.contains(v)
    }

  lazy val (continents, islands, isles, atolls) = {
    val isl = Islands.all.toSet
    val continents = Set(Landmass(
      IslandClass.Continent,
      isl.maxBy {
        _.size
      },
      Some(randNoun),
      None,
    ))
    val totalSize = Islands.all.map {
      _.size
    }.sum - continents.head.area

    val wIsl = isl - continents.head.tiles

    val islands = wIsl.filter {
      _.size.toFloat / totalSize > ISLAND_THRESHOLD
    }.map(tiles => Landmass(IslandClass.Island, tiles, Some(randNoun), None))

    val isles = wIsl.filter { elem =>
      val sz = elem.size.toFloat / totalSize
      sz > ISLE_THRESHOLD && sz <= ISLAND_THRESHOLD
    }.map(tiles => Landmass(IslandClass.Isle, tiles, Some(randNoun), None))

    val atolls = wIsl.filter { elem =>
      val sz = elem.size.toFloat / totalSize
      sz <= ISLE_THRESHOLD
    }.map(tiles => Landmass(IslandClass.Atoll, tiles, Some(randNoun), None))

    (continents, islands, isles, atolls)
  }

  lazy val all: Set[Landmass] = continents ++ islands ++ isles ++ atolls

  private def randNoun = nouns(random.int(0, nouns.length - 1)).titleCase

  private val nouns = nounsScope.nouns

  /**
   * This is the best way I could find to include a resource like this.
   * See resources/nouns.js for the full list.
   */
  @JSGlobalScope
  @js.native
  private object nounsScope extends js.Object {

    @JSName("NOUNS")
    val nouns: js.Array[String] = js.native

  }

}
