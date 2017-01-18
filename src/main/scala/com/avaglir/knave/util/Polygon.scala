package com.avaglir.knave.util

import scala.annotation.tailrec

case class Polygon(points: IntVec*) {
  private lazy val horiz = points map { _.x }
  private lazy val vert = points map { _.y }

  @tailrec
  private def precompute(i: Int, j: Int, const: List[Int], mult: List[Int]): (Array[Int], Array[Int]) = i match {
    case _ if i == points.length => (const.toArray, mult.toArray)
    case _ if vert(i) == vert(j) => precompute(i + 1, i, horiz(i) :: const, 0 :: mult)
    case _ =>
      val k = horiz(i) - (vert(i) * horiz(j)) / (vert(j) - vert(i)) + (vert(i) * horiz(i)) / (vert(j) - vert(i))
      val m = (horiz(j) - horiz(i)) / (vert(j) - vert(i))
      precompute(i + 1, i, k :: const, m :: mult)
  }

  private lazy val (const, mul) = precompute(0, points.length - 1, List.empty, List.empty)

  def contains(point: IntVec) = {
    @tailrec
    def inside(i: Int, j: Int, oddNodes: Boolean): Boolean = {
      i match {
        case _ if i == points.length => oddNodes
        case _ if vert(i) < point.y && vert(j) >= point.y || vert(j) < point.y && vert(i) >= point.y =>
          val odd = oddNodes ^ (point.y * mul(i) + const(i) < point.x)
          inside(i + 1, i, odd)
        case _ => inside(i + 1, i, oddNodes)
      }
    }

    inside(0, points.length - 1, oddNodes = false)
  }

  def svgPath: String = "M" + this.points.map { point => s"${point.x},${point.y}" }.mkString(" ") + "z"
}

object Polygon {
  import com.thesamet.spatial._
  private implicit val ivOrd = new DimensionalOrdering[IntVec] {
    /** How many dimensions type A has. */
    override def dimensions: Int = 2

    /** Returns an integer whose sign communicates how x's projection on a given dimension compares
      * to y's.
      *
      * Denote the projection of x and y on `dimension` by x' and y' respectively. The result sign has
      * the following meaning:
      *
      * - negative if x' < y'
      * - positive if x' > y'
      * - zero if x' == y'
      */
    override def compareProjection(dimension: Int)(x: IntVec, y: IntVec): Int = dimension match {
      case 0 => x.x.compare(y.x)
      case 1 => x.y.compare(y.y)
      case _ => throw new IllegalArgumentException
    }
  }

  implicit val euclideanMetric = new Metric[IntVec, Double] {
    /** Returns the distance between two points. */
    override def distance(x: IntVec, y: IntVec): Double = (x - y).magnitude

    /** Returns the distance between x and a hyperplane that passes through y and perpendicular to
      * that dimension.
      */
    override def planarDistance(dimension: Int)(x: IntVec, y: IntVec): Double = dimension match {
      case 0 => x.x - y.x
      case 1 => x.y - y.y
      case _ => throw new IllegalArgumentException
    }
  }

  def fromUnordered(pts: IntVec*): Polygon = {
    if (pts.isEmpty) return Polygon()
    val tree = KDTree(pts: _*)

    val nearest = pts.map { pt =>
      val nearest = tree.findNearest(pt, 3).filter { _ != pt}
      (pt, nearest)
    }.toMap

    def pbfs[S](init: S, expand: S => List[S]): List[S] = {
      @tailrec
      def search(unexpanded: List[S], seen: List[S]): List[S] = unexpanded match {
        case Nil => seen
        case elem :: tail if seen contains elem => search(tail, seen)
        case elem :: tail => search(expand(elem) ++ tail, elem :: seen)
      }

      search(List(init), List.empty)
    }

    Polygon(pbfs(pts.head, (elem: IntVec) => elem.adjacent.intersect(pts)): _*)
  }

}
