package com.avaglir.knave.map

import simplex.SimplexNoise

object Simplex {
  private lazy val simplexNoise = SimplexNoise()

  def generate(
      width: Int,
      height: Int,
      threshold: Double = 0.5,
    ) =
    (0 until width)
      .map { x =>
        (0 until height)
          .map { y =>
            if (simplexNoise.eval(x, y) > threshold) Tile.FLOOR
            else Tile.WALL
          }
          .toArray
      }
      .toArray

}
