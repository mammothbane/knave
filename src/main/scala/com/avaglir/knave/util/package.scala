package com.avaglir.knave

import scala.annotation.tailrec
import scala.collection.mutable

package object util {
  type IntVec = Vector2[Int]

  def circle_simple(center: Vector2[Int], radius: Int): List[Vector2[Int]] = {
    val sorted = midpoint(center, radius).groupBy {
      _.x
    }.values.map { xls =>
      (
        xls.minBy {
          _.y
        },
        xls.maxBy {
          _.y
        },
      )
    }

    sorted.flatMap {
      case (min, max) => (min.y to max.y).map {
          Vector2(min.x, _)
        }
    }.toList
  }

  // midpoint algorithm, borrowed from rosetta code:
  // https://rosettacode.org/wiki/Bitmap/Midpoint_circle_algorithm#Scala
  def midpoint(center: Vector2[Int], radius: Int): List[Vector2[Int]] = {
    var f     = 1 - radius
    var ddF_x = 1
    var ddF_y = -2 * radius
    var x     = 0
    var y     = radius

    val out = new mutable.ListBuffer[Vector2[Int]]

    out += Vector2(center.x, center.y + radius)
    out += Vector2(center.x, center.y - radius)
    out += Vector2(center.x + radius, center.y)
    out += Vector2(center.x - radius, center.y)

    while (x < y) {
      if (f >= 0) {
        y -= 1
        ddF_y += 2
        f += ddF_y
      }

      x += 1
      ddF_x += 2
      f += ddF_x

      out += Vector2(center.x + x, center.y + y)
      out += Vector2(center.x - x, center.y + y)
      out += Vector2(center.x + x, center.y - y)
      out += Vector2(center.x - x, center.y - y)
      out += Vector2(center.x + y, center.y + x)
      out += Vector2(center.x - y, center.y + x)
      out += Vector2(center.x + y, center.y - x)
      out += Vector2(center.x - y, center.y - x)
    }

    out.toList
  }

  def bresenhamLine(from: Vector2[Int], to: Vector2[Int]): List[Vector2[Int]] = {
    val out = mutable.ListBuffer.empty[Vector2[Int]]

    val tDelta = to - from
    var delta  = tDelta.map(math.abs)
    if (delta.x < delta.y) delta = delta.transpose

    var dErr = math.abs(delta.y.toFloat / delta.x)
    var err  = dErr - 0.5

    var y = 0

    for (x <- 0 to delta.x) {
      out += Vector2(x, y)

      err += dErr
      if (err >= 0.5) {
        y += 1
        err -= 1
      }
    }

    (tDelta.octant match {
      case 1 | 0 => out
      case 2 => out.map {
          _.transpose
        }
      case 3 => out.reverse.map(elem => Vector2(-elem.y, elem.x))
      case 4 => out.reverse.map(elem => Vector2(-elem.x, elem.y))
      case 5 => out.reverse.map(-_)
      case 6 => out.reverse.map(_.transpose * -1)
      case 7 => out.map(elem => Vector2(elem.y, -elem.x))
      case 8 => out.map(elem => Vector2(elem.x, -elem.y))
    }).map(elem => elem + from).toList
  }

  // found here: https://github.com/raboof/astar/blob/master/src/main/scala/Astar.scala
  def aStar[S](
      init: S,
      expand: S => Set[S],
      h: S => Float,
      value: S => Float,
    ): S = {
    @tailrec
    def search(
        unexpanded: Set[S],
        best: S,
        seen: Set[S],
      ): S =
      unexpanded.toList.map(s => (s, h(s))).sortBy { case (_, heur) => -heur }.headOption match {
        case None                                   => best
        case Some((_, heur)) if heur <= value(best) => best
        case Some((exp, _)) =>
          val expanded = expand(exp).diff(seen)
          val result   = if (value(exp) > value(best)) exp else best
          val bestV    = value(result)

          search(
            (unexpanded.filter(_ != exp) ++ expanded).filter(h(_) > bestV),
            result,
            (seen + exp).filter {
              h(_) > bestV
            },
          )
      }

    search(Set(init), init, Set.empty)
  }

  def bfs[S](init: S, expand: S => List[S]): Set[S] = {
    @tailrec
    def search(unexpanded: List[S], seen: Set[S]): Set[S] =
      unexpanded match {
        case Nil                                => seen
        case elem :: tail if seen contains elem => search(tail, seen)
        case elem :: tail                       => search(expand(elem) ++ tail, seen + elem)
      }

    search(List(init), Set.empty)
  }

  /**
   * Computes the distinct components of the graph represented by elems. Quadratic in time with no optimization.
   *
   * @param elems     The graph.
   * @param connected A function that determines whether two nodes in the graph are connected. Assumed to be transitive.
   * @tparam S The node type.
   * @return A list of connected components.
   */
  def components[S](elems: Set[S], connected: (S, S) => Boolean): List[Set[S]] = {
    @tailrec
    def buildComponent(remaining: Set[S], existing: List[Set[S]]): List[Set[S]] =
      remaining match {
        case x if x.isEmpty => existing
        case _ =>
          val component = remaining.tail.filter {
            connected(remaining.head, _)
          } + remaining.head
          buildComponent(remaining.diff(component), component :: existing)
      }

    buildComponent(elems, Nil)
  }

  def seededRandom(implicit seed: RandomSeed): rot.RNG = {
    val rand = rot.RNG.clone()
    rand.setSeed(seed.value)
    rand
  }

  /**
   * Sample from a poisson distribution with expected value lambda.
   * Note: this implementation uses the Knuth algorithm, so it is slow for large lambda.
   *
   * @param lambda The expected value.
   */
  def poisson(lambda: Int)(implicit rng: rot.RNG): Int = {
    val l         = math.exp(-lambda)
    var k         = 0
    var p: Double = 1

    do {
      k += 1
      p *= rng.uniform()
    } while (p > l)

    k - 1
  }

  implicit class ary2dExt[T](a: Array[Array[T]]) {
    def apply(x: IntVec): T = a(x.x)(x.y)

    def extents: Vector2[Int] = Vector2(a.length, a.head.length)
  }

  implicit class genTExt[W](a: Iterable[W]) {

    def cartesianProduct[Z](other: Iterable[Z]): Iterable[(W, Z)] =
      a.flatMap(elem => other.map(inner => (elem, inner)))
  }

  @inline def maxOf[T: Ordering](args: T*): T = args.max

  @inline def minOf[T: Ordering](args: T*): T = args.min

  val hexArray: Array[Char] = "0123456789abcdef".toCharArray

  @inline def bytesToHex(bytes: Array[Byte], debug: Boolean = false): String = {
    val hexChars = new Array[Char](bytes.length * 2)
    for (j <- bytes.indices) {
      val v = bytes(j) & 0xff
      hexChars(j * 2) = hexArray(v >>> 4)
      hexChars(j * 2 + 1) = hexArray(v & 0x0f)
    }
    new String(hexChars)
  }

  implicit class numericExt[T: Numeric](t: T) {
    private val num = implicitly[Numeric[T]]

    import num._

    def clamp: T = clamp(zero, one)

    def clamp(min: T, max: T): T = if (t < min) min else if (t > max) max else t
  }

  implicit class floatExt(f: Float) {
    def unitClamped: UnitClampedFloat = UnitClampedFloat(f)
  }

  implicit class strCtxExt(s: StringContext) {
    def c(args: Any*): Color = Color(s.parts.mkString)
  }

  final private val colorRegex = "%c{#[0-9a-f]{6}}%b{#[0-9a-f]{6}}".r

  implicit class strExt(s: String) {

    def colorize(fg: Color = Color.WHITE, bg: Color = Color.BLACK): String =
      s"%c{${fg.hex}}%b{${bg.hex}}$s%c{${Color.WHITE.hex}}%b{${Color.BLACK.hex}}"

    def decolorize: String = colorRegex.replaceAllIn(s, "")

    def titleCase: String =
      s match {
        case ""                 => s
        case x if x.length == 1 => x.toUpperCase
        case _                  => s(0).toUpper + s.substring(1)
      }

  }

  implicit def clamped2Float(c: ClampedFloat): Float = c.value

  implicit def float2UnitClamped(f: Float): UnitClampedFloat = UnitClampedFloat(f)
}
