package com.avaglir.knave

import scala.collection.mutable

package object util {
  def circle_simple(center: Vector2, radius: Int): List[Vector2] = {
    val sorted = midpoint(center, radius).groupBy { _.x }.values.map { xls => (xls.minBy { _.y }, xls.maxBy { _.y } ) }

    sorted.flatMap {
      case (min, max) => (min.y to max.y).map { Vector2(min.x, _) }
    }.toList
  }

  // midpoint algorithm, borrowed from rosetta code:
  // https://rosettacode.org/wiki/Bitmap/Midpoint_circle_algorithm#Scala
  def midpoint(center: Vector2, radius: Int): List[Vector2] = {
    var f = 1 - radius
    var ddF_x = 1
    var ddF_y = -2*radius
    var x = 0
    var y = radius

    val out = new mutable.ListBuffer[Vector2]

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

  def bresenhamLine(from: Vector2, to: Vector2): List[Vector2] = {
    val out = mutable.ListBuffer[Vector2]()

    val tDelta = to - from
    var delta = tDelta.map(math.abs)
    if (delta.x < delta.y) delta = delta.transpose

    var dErr = math.abs(delta.y.toFloat / delta.x)
    var err = dErr - 0.5

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
      case 2 => out.map { _.transpose }
      case 3 => out.reverse.map { elem => Vector2(-elem.y, elem.x) }
      case 4 => out.reverse.map { elem => Vector2(-elem.x, elem.y) }
      case 5 => out.reverse.map { -_ }
      case 6 => out.reverse.map { _.transpose * -1 }
      case 7 => out.map { _.transpose * -1 }
      case 8 => out.map { elem => Vector2(elem.x, -elem.y) }
    }).map{ elem => elem + from }.toList
  }

  def maxOf[T: Ordering](args: T*): T = args.max
  def minOf[T: Ordering](args: T*): T = args.min

  implicit class floatExt(f: Float) {
    def clamp: Float = clamp(0f, 1f)
    def clamp(min: Float, max: Float): Float = if (f < min) min else if (f > max) max else f

    def unitClamped = new UnitClampedFloat(f)
  }

  private final val colorRegex = "%c{#[0-9a-f]{6}}%b{#[0-9a-f]{6}}".r
  implicit class strExt(s: String) {
    def colorize(fg: Color = Color.WHITE, bg: Color = Color.BLACK): String = s"%c{${fg.hex}}%b{${bg.hex}}$s"
    def decolorize: String = colorRegex.replaceAllIn(s, "")
  }

  implicit def clamped2Float(c: ClampedFloat): Float = c.value
  implicit def float2UnitClamped(f: Float): UnitClampedFloat = new UnitClampedFloat(f)
}
