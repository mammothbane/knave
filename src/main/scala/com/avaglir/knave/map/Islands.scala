package com.avaglir.knave.map

import com.avaglir.knave.util._

import scala.collection.mutable

object Islands {
  val threshold = 0.35

  def edges: List[Set[IntVec]] = {
    val dimen = 250
    val bounds = Vector2.UNIT[Int] * dimen
    val chunks = GenMap.emit(dimen)

    def boundary(v: IntVec): Boolean = chunks(v) >= threshold && v.adjacent(true).exists { elem => elem.componentsClamped(bounds) && chunks(elem) < threshold }

    val vecs = chunks.indices.cartesianProduct(chunks.indices).map(Vector2.apply[Int]).toSet



    val out = mutable.ListBuffer.empty[Set[Vector2[Int]]]

    // run through all pixels
    for (x <- 0 until dimen; y <- 0 until dimen) {
      val vec = Vector2(x, y)
      if (boundary(vec) && !out.exists { _ contains vec }) {
        out += bfs[Vector2[Int]](vec, { v =>
          v.adjacent(true).filter(boundary)
        })
      }
    }

    out.toList
  }

  lazy val all: List[Set[IntVec]] = {
//    val dimen = GenMap.DIMENS / 4
    val dimen = 250
    val chunks = GenMap.emit(dimen)

    val out = mutable.ListBuffer.empty[Set[Vector2[Int]]]

    for (x <- 0 until dimen; y <- 0 until dimen) {
      val vec = Vector2(x, y)
      if (!out.exists { _ contains vec } && chunks(x)(y) >= threshold) {
        out += bfs(vec, { v =>
          v.adjacent.filter { vec =>
            chunks(vec.x)(vec.y) >= threshold &&
              vec.x >= 0 && vec.x < dimen && vec.y >= 0 && vec.y < dimen
          }
        })
      }
    }

    out.toList
  }
}
