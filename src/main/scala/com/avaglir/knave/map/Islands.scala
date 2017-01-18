package com.avaglir.knave.map

import com.avaglir.knave.util._

import scala.collection.mutable

object Islands {
  val threshold = 0.35
  private val DIMEN = 250
  val bounds = Vector2.UNIT[Int] * DIMEN

  private def boundary(v: IntVec): Boolean = GenMap(v, DIMEN) >= threshold && v.adjacent(true).exists { elem => elem.componentsClamped(bounds) && GenMap(elem, DIMEN) < threshold }

  lazy val edges: List[Set[IntVec]] = all map { island => island.filter(boundary) }

  lazy val all: List[Set[IntVec]] = {
    val dimen = 250
    val chunks = GenMap.emit(dimen)

    val out = mutable.ListBuffer.empty[Set[Vector2[Int]]]

    for (x <- 0 until dimen; y <- 0 until dimen) {
      val vec = Vector2(x, y)
      if (chunks(vec) >= threshold && !out.exists { _ contains vec }) {
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
