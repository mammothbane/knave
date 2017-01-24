package com.avaglir.knave.map

import com.avaglir.knave.util._

case class Chunk(location: Vector2[Int], tiles: Array[Array[Tile]]) {

  def apply(v: Vector2[Int]) = tiles(v)
}

object Chunk {
  val DIMENS = 256

  @cache(25, None)
  def apply(location: Vector2[Int]): Chunk = {
    assert(location.componentsClamped(Vector2.UNIT[Int] * World.DIMENS))
    val tiles = Array.ofDim[Tile](DIMENS, DIMENS)

    (0 until DIMENS).cartesianProduct(0 until DIMENS).foreach { case (x, y) =>
      val loc = Vector2(x, y) + location
      tiles(x)(y) = if (World(loc) >= Islands.threshold) {
        Tile.FLOOR
      } else {
        Tile.WATER
      }
    }

    Chunk(location, tiles)
  }
}
