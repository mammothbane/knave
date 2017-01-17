package com.avaglir.knave.map

import com.avaglir.knave.util._

class Chunk(seed: Float) {
  import Chunk._

  private val tiles = Array.ofDim[Tile](DIMENS, DIMENS)

  def apply(v: IntVec) = {
    if (v.x >= DIMENS || v.y >= DIMENS) throw new IllegalArgumentException
    tiles(v.x)(v.y)
  }
}

object Chunk {
  val DIMENS = 256
}