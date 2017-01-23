package com.avaglir.knave.map

import com.avaglir.knave.util._

case class Chunk(location: IntVec, tiles: Array[Array[Tile]]) {

  def apply(v: IntVec) = tiles(v)
}

object Chunk {
  val DIMENS = 256

  @cache(25, None)
  def apply(location: IntVec): Chunk = {
    println("computing new chunk")
    Chunk(location, Array.ofDim[Tile](DIMENS, DIMENS))
  }
}
