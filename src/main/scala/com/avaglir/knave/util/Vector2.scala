package com.avaglir.knave.util

case class Vector2(x: Int, y: Int) {
  def +(other: Vector2) = Vector2(x + other.x, y + other.y)
  def -(other: Vector2) = Vector2(x - other.x, y - other.y)
  def dot(other: Vector2) = x * other.x + y * other.y

  def unary_- = this.map(elem => -elem)

  def *(factor: Float) = map { (elem) => (elem * factor).toInt }
  def /(factor: Float) = map { (elem) => (elem * factor).toInt }

  def half = this / 2
  def magnitude: Float = math.sqrt(x*x + y*y).toFloat
  def normalize = this / magnitude
  def transpose = Vector2(y, x)

  def map(fn: (Int) => Int) = Vector2(fn(x), fn(y))

  override def equals(obj: scala.Any): Boolean = {
    if (!obj.isInstanceOf[Vector2]) return false

    val other = obj.asInstanceOf[Vector2]
    other.x == x && other.y == y
  }

  override def toString: String = s"v2($x, $y)"

  def <(other: Vector2): Boolean = x < other.x && y < other.y
  def >(other: Vector2): Boolean = x > other.y && y > other.y

  def octant: Int = {
    if (x > 0 && y >= 0) { // first quadrant
      if (x >= y) 1
      else 2
    } else if (y > 0 && x <= 0) {  // second quadrant
      if (-x >= y) 4
      else 3
    } else if (y <= 0 && x < 0) { // third quadrant
      if (-x >= -y) 5
      else 6
    } else if (y < 0 && x >= 0) { // fourth quadrant
      if (x >= -y) 8
      else 7
    } else 0
  }
}

object Vector2 {
  val ZERO = Vector2(0, 0)
  val UNIT = Vector2(1, 1)

  val UP = Vector2(0, -1)
  val DOWN = Vector2(0, 1)
  val LEFT = Vector2(-1, 0)
  val RIGHT = Vector2(1, 0)
}