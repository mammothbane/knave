package com.avaglir.knave.map

import com.avaglir.knave.util._

import scala.collection.mutable

object Islands {
  val threshold = 0.35

  def edges: List[Set[Vector2]] = {
    val dimen = 250
//    val chunks = GenMap.locs(dimen).filter { case (_, v) => v >= threshold }
    val chunks = GenMap.emit(dimen)
    val bounds = Vector2.UNIT * dimen

    def boundary(v: Vector2): Boolean = chunks(v.x)(v.y) >= threshold && v.adjacent(true).exists { elem => elem.componentsClamped(bounds) && chunks(elem.x)(elem.y) < threshold }

    val out = mutable.ListBuffer.empty[Set[Vector2]]

    // run through all pixels
    for (x <- 0 until dimen; y <- 0 until dimen) {
      val vec = Vector2(x, y)
      if (boundary(vec) && !out.exists { _ contains vec }) {
        out += bfs[Vector2](vec, { v =>
          v.adjacent(true).filter(boundary)
        })
      }
    }

    out.toList
  }

  lazy val all: List[Set[Vector2]] = {
//    val dimen = GenMap.DIMENS / 4
    val dimen = 250
    val chunks = GenMap.emit(dimen)

    val out = mutable.ListBuffer.empty[Set[Vector2]]

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
