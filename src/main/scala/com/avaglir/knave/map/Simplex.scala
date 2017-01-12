package com.avaglir.knave.map

import com.avaglir.knave.util.Persist
import simplex.SimplexNoise

object Simplex extends TileGenerator with Persist {
  private var simplexNoise = SimplexNoise()

  def generate(width: Int, height: Int, threshold: Double): Array[Array[Tile]] = {
    (0 until width).map { x =>
      (0 until height).map { y => if (simplexNoise.eval(x, y) > threshold) Tile.FLOOR else Tile.WALL }.toArray
    }.toArray
  }

  override def generate(width: Int, height: Int): Array[Array[Tile]] = {
    generate(width, height, 0.5)
  }

  import com.avaglir.knave.util.storage.Pickling._
  import prickle._
  override def persist(): Map[Symbol, String] = Map(
    'noise -> Pickle.intoString(simplexNoise)
  )

  override def restore(v: Map[Symbol, String]): Unit = {
    this.simplexNoise = Unpickle[SimplexNoise].fromString(v('noise)).get
  }

  override val key: Symbol = 'simplex
}
