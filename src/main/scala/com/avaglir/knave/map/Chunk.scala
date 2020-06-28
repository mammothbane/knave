package com.avaglir.knave.map

import com.avaglir.knave.util._

import scala.collection.mutable

case class Chunk(location: Vector2[Int], tiles: Array[Array[Tile]]) {
    import Chunk._

    val remembered = mutable.Set.empty[Vector2[Int]]

    def view(v: Vector2[Int]*) = v.foreach(remembered.add)

    def apply(v: Vector2[Int]) = {
        assert(v.componentsClamped(Vector2.UNIT[Int] * DIMENS))
        tiles(v)
    }

    override def hashCode(): Int = location.hashCode
}

object Chunk {
    val DIMENS = 256
    val TILE_DIMENS = DIMENS * World.DIMENS

    /**
     * Get the chunk at the given Chunk coordinates.
     */
    def apply(location: Vector2[Int]): Chunk = {
        require(location.componentsClamped(Vector2.UNIT[Int] * World.DIMENS))

        val tiles = Array.ofDim[Tile](DIMENS, DIMENS)
        val fullLoc = location * DIMENS

        val pairs = (0 until DIMENS).cartesianProduct(0 until DIMENS)

        pairs.foreach { case (x, y) =>
            val loc = Vector2(x, y) + fullLoc

            tiles(x)(y) =
                if (World(loc, TILE_DIMENS) >= Islands.threshold) { Tile.FLOOR }
                else { Tile.WATER }
        }

        Chunk(location, tiles)
    }
}
