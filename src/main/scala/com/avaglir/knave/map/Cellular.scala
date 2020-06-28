package com.avaglir.knave.map

import com.avaglir.knave.Knave
import com.avaglir.knave.util._

object Cellular extends TileGenerator {
    private val birth = List(5, 6, 7, 8)
    private val survive = List(4, 5, 6, 7, 8)

    def generate(width: Int, height: Int): Array[Array[Tile]] = generate(width, height, 0.6f)

    def generate(width: Int, height: Int, aliveProbability: UnitClampedFloat, generations: Int = 4): Array[Array[Tile]] = {
        var cur = Array.ofDim[Int](width, height)
        var next = Array.fill(width, height) {
            if (Knave.random.uniform() > aliveProbability) 0 else 1
        }

        def locValue(x: Int, y: Int): Int = {
            if (x < 0 || x >= width) return 0
            if (y < 0 || y >= height) return 0
            cur(x)(y)
        }

        def neighborCount(x: Int, y: Int): Int =
            locValue(x + 1, y) +
                locValue(x - 1, y) +
                locValue(x, y + 1) +
                locValue(x, y - 1) +
                locValue(x + 1, y + 1) +
                locValue(x - 1, y - 1) +
                locValue(x - 1, y + 1) +
                locValue(x + 1, y - 1)

        for (i <- 0 until generations) {
            val tmp = cur
            cur = next
            next = tmp

            for (x <- 0 until width; y <- 0 until height) {
                val neighbors = neighborCount(x, y)

                if (cur(x)(y) != 0 && survive.contains(neighbors) ||
                    cur(x)(y) == 0 && birth.contains(neighbors)) {
                    next(x)(y) = 1
                }
                else next(x)(y) = 0
            }
        }

        next.map {
            _.map {
                case 1 => Tile.FLOOR
                case 0 => Tile.WALL
            }.toArray
        }
    }
}
