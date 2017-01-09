package com.avaglir.knave

import scala.collection.mutable

package object util {
  def circle_simple(center: Vector2, radius: Int): List[Vector2] = {
    val sorted = midpoint(center, radius).groupBy { _.x }.values.map { xls => (xls.minBy { _.y }, xls.maxBy { _.y } ) }

    val out = new mutable.ListBuffer[Vector2]

    sorted.foreach {
      case (min, max) =>
        for (i <- min.y to max.y) {
          out += Vector2(min.x, i)
        }
    }

    out.toList
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

    while(x < y) {
      if(f >= 0) {
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

    println(s"from: $from, to: $to")

    val tDelta = to - from
    var delta = tDelta.map(math.abs)
    if (delta.x < delta.y) delta = delta.transpose
    println(s"calcDelta: $delta")

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

    println(s"delta: $tDelta, octant: ${tDelta.octant}")

    val a = (tDelta.octant match {
      case 1 => out
      case 2 => out.map { _.transpose }
      case 3 => out.reverse.map { elem => Vector2(-elem.y, elem.x) }
      case 4 => out.reverse.map { elem => Vector2(-elem.x, elem.y) }
      case 5 => out.reverse.map { -_ }
      case 6 => out.reverse.map { _.transpose * -1 }
      case 7 => out.map { _.transpose * -1 }
      case 8 => out.map { elem => Vector2(elem.x, -elem.y) }
    }).map{ elem => elem + from }.toList

    println(a)

    a
  }

  def maxOf[T: Ordering](args: T*): T = args.max
  def minOf[T: Ordering](args: T*): T = args.min

  implicit class floatExt(f: Float) {
    def clamp: Float = clamp(0f, 1f)
    def clamp(min: Float, max: Float): Float = if (f < min) min else if (f > max) max else f
  }

  implicit def clamped2Float(c: ClampedFloat): Float = c.value
  implicit def float2UnitClamped(f: Float): UnitClampedFloat = new UnitClampedFloat(f)
}
