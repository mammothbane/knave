package com.avaglir.knave.map

import com.avaglir.knave.util._

case class Chunk(location: IntVec, tiles: Array[Array[Tile]]) {

  def apply(v: IntVec) = tiles(v)
}

object Chunk {
  val DIMENS = 256
  val cache =


  def apply(location: IntVec): Chunk = {
    val ialands
  }
}
