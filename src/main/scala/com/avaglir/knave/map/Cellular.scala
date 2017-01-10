package com.avaglir.knave.map

import com.avaglir.knave.Knave
import com.avaglir.knave.util._

import scala.collection.mutable

object Cellular {
  val birth = List(5, 6, 7, 8)
  val death = List(4, 5, 6, 7, 8)

  def generate(width: Int, height: Int, aliveProbability: ClampedFloat, generations: Int = 4): Array[Array[Tile]] = {
    mutable.ArrayBuffer.fill(width, height)(Knave.random.uniform() < aliveProbability)

    var cur = mutable.ArrayBuffer(Array.ofDim[Int](width, height): _*)


    for (x <- 0 to width; y <- 0 to height) {

    }

    for (i <- 0 to generations) {
      val next = Array.ofDim[Int](width, height)

      for (x <- 0 to width; y <- 0 to height) {

      }
    }


    cur.toArray

    Array.ofDim[Tile](3, 3)
  }
}
