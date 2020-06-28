package com.avaglir.knave.map

import com.avaglir.knave.util._

import scala.collection.mutable

object Islands {
    val threshold = 0.36
    val SCALE = 8
    val DIMENS = World.DIMENS / SCALE
    val bounds = Vector2.UNIT[Int] * DIMENS

    private implicit def double2Int(d: Double): Int = d.toInt

    private def boundary(v: IntVec): Boolean = {
        World(v, DIMENS) >= threshold && v.adjacent(true).exists { elem => elem.componentsClamped(bounds) && World(elem, DIMENS) < threshold }
    }

    lazy val edges: List[Set[IntVec]] = edgesRes(DIMENS)

    def edgesRes(resolution: Int): List[Set[Vector2[Int]]] = allRes(resolution).map { island =>
        island.map { pt => (pt * resolution).as[Int] }.filter(boundary)
    }

    /**
     * A list of islands at reduced resolution.
     */
    lazy val all: List[Set[Vector2[Int]]] = allRes(DIMENS)

    def allRes(resolution: Int): List[Set[Vector2[Int]]] = {
        val chunks = World.emit(resolution)
        val out = mutable.ListBuffer.empty[Set[Vector2[Int]]]

        for (x <- 0 until resolution; y <- 0 until resolution) {
            val vec = Vector2(x, y)
            if (chunks(vec) >= threshold && !out.exists {
                _ contains vec
            }) {
                out += bfs(vec, { v =>
                    v.adjacent.filter { vec =>
                        chunks(vec) >= threshold && vec.componentsClamped(Vector2(resolution, resolution))
                    }
                })
            }
        }

        out.toList
    }
}
