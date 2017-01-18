package com.avaglir.knave.util

import scala.annotation.tailrec

case class Polygon(points: Set[IntVec]) {
  private lazy val ptList = points.toList

  private lazy val horiz = ptList map { _.x }
  private lazy val vert = ptList map { _.y }

  @tailrec
  private def precompute(i: Int, j: Int, const: List[Int], mult: List[Int]): (Array[Int], Array[Int]) = i match {
    case _ if i == ptList.length => (const.toArray, mult.toArray)
    case _ if vert(i) == vert(j) => precompute(i + 1, i, horiz(i) :: const, 0 :: mult)
    case _ =>
      val k = horiz(i) - (vert(i) * horiz(j)) / (vert(j) - vert(i)) + (vert(i) * horiz(i)) / (vert(j) - vert(i))
      val m = (horiz(j) - horiz(i)) / (vert(j) - vert(i))
      precompute(i + 1, i, k :: const, m :: mult)
  }

  private lazy val (const, mul) = precompute(0, ptList.length - 1, List.empty, List.empty)

  def contains(point: IntVec) = {
    @tailrec
    def inside(i: Int, j: Int, oddNodes: Boolean): Boolean = {
      i match {
        case _ if i == ptList.length => oddNodes
        case _ if vert(i) < point.y && vert(j) >= point.y || vert(j) < point.y && vert(i) >= point.y =>
          val odd = oddNodes ^ (point.y * mul(i) + const(i) < point.x)
          inside(i + 1, i, odd)
        case _ => inside(i + 1, i, oddNodes)
      }
    }

    inside(0, ptList.length - 1, oddNodes = false)
  }

  def svgPath: String = "M" + this.points.map { point => s"${point.x},${point.y}" }.mkString(" ") + "z"
}
