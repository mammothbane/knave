package com.avaglir.knave.map

/**
  * Created by mammothbane on 1/9/2017.
  */
object Cellular {
  val birth = List(5, 6, 7, 8)
  val death = List(4, 5, 6, 7, 8)

  def generate(width: Int, height: Int, generations: Int = 4): Array[Array[Tile]] = {
    var cur = Array.ofDim[Int](width, height)

    for (i <- 0 to generations; x <- 0 to width; y <- 0 to height) {

    }

    cur
  }
}
