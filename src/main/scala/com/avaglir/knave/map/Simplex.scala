package com.avaglir.knave.map

import simplex.SimplexNoise

object Simplex extends TileGenerator {
  private val simplexNoise = SimplexNoise()

  def generate(width: Int, height: Int, threshold: Double): Array[Array[Tile]] = {
    (0 until width).map { x =>
      (0 until height).map { y => if (simplexNoise.eval(x, y) > threshold) Tile.FLOOR else Tile.WALL }.toArray
    }.toArray
  }

  override def generate(width: Int, height: Int): Array[Array[Tile]] = {
    generate(width, height, 0.5)
  }
}
