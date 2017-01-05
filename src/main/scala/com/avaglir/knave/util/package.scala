package com.avaglir.knave

import scala.collection.mutable

package object util {
  // midpoint algorithm, borrowed from rosetta code:
  // https://rosettacode.org/wiki/Bitmap/Midpoint_circle_algorithm#Scala
  def midpoint(center: Vector2, radius: Int): List[Vector2] = {
    var f = 1 - radius
    var ddF_x = 1
    var ddF_y = -2*radius
    var x = 0
    var y = radius

    val out = new mutable.ListBuffer[Vector2]()

    out += Vector2(center.x, center.y + radius)
    out += Vector2(center.x, center.y - radius)
    out += Vector2(center.x + radius, center.y)
    out += Vector2(center.x - radius, center.y)

    while(x < y)
    {
      if(f >= 0)
      {
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

}
