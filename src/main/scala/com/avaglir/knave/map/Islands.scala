package com.avaglir.knave.map

import com.avaglir.knave.util._

import scala.collection.mutable

object Islands {
  val threshold = 0.36
  private val DIMEN = 512
  val bounds = Vector2.UNIT[Int] * DIMEN

  private implicit def double2Int(d: Double): Int = d.toInt
  private def boundary(v: IntVec): Boolean = GenMap(v, DIMEN) >= threshold && v.adjacent(true).exists { elem => elem.componentsClamped(bounds) && GenMap(elem, DIMEN) < threshold }

  lazy val edges: List[Set[IntVec]] = edgesRes(DIMEN)

  def edgesRes(resolution: Int): List[Set[Vector2[Int]]] = allRes(resolution).map { island =>
    island.map { pt => (pt * resolution).as[Int] }.filter(boundary)
  }

  /**
    * Return all islands, rendered initially at a resolution of 250x250.
    */
  lazy val all: List[Set[Vector2[Double]]] = allRes(DIMEN)

  def allRes(resolution: Int): List[Set[Vector2[Double]]] = {
    val chunks = GenMap.emit(resolution)
    val out = mutable.ListBuffer.empty[Set[Vector2[Int]]]

    for (x <- 0 until resolution; y <- 0 until resolution) {
      val vec = Vector2(x, y)
      if (chunks(vec) >= threshold && !out.exists { _ contains vec }) {
        out += bfs(vec, { v =>
          v.adjacent.filter { vec =>
            chunks(vec) >= threshold && vec.componentsClamped(Vector2(resolution, resolution))
          }
        })
      }
    }

    out.toList.map{ _.map { _.as[Double] / resolution } }
  }
}
