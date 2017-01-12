package com.avaglir.knave.map

import com.avaglir.knave.util._
import simplex.SimplexNoise

object GenMap extends Persist {
  var large = SimplexNoise()
  var small = SimplexNoise()

  def apply(x: Vector2): Unit = apply(x.x, x.y)
  def apply(x: Int, y: Int): Unit = {

  }

  import com.avaglir.knave.util.storage.Pickling._
  import prickle._
  override def persist(): Map[Symbol, String] = Map(
    'simplex_large -> Pickle.intoString(large),
    'simplex_small -> Pickle.intoString(small)
  )

  override def restore(v: Map[Symbol, String]): Unit = {
    large = Unpickle[SimplexNoise].fromString(v('simplex_large)).get
    small = Unpickle[SimplexNoise].fromString(v('simplex_small)).get
  }
  override def key: Symbol = 'mapgen
}
