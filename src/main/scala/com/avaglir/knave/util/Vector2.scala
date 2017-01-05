package com.avaglir.knave.util

case class Vector2(x: Int, y: Int) {
  def +(other: Vector2) = Vector2(x + other.x, y + other.y)
  def -(other: Vector2) = Vector2(x - other.x, y - other.y)
  def dot(other: Vector2) = x * other.x + y * other.y

  def *(factor: Float) = map { (elem) => (elem * factor).toInt }
  def /(factor: Float) = map { (elem) => (elem * factor).toInt }

  def half = this / 2
  def magnitude: Float = math.sqrt(x*x + y*y).toFloat
  def normalize = this / magnitude

  def map(fn: (Int) => Int) = Vector2(fn(x), fn(y))
}

object Vector2 {
  val ZERO = Vector2(0, 0)
  val UNIT = Vector2(1, 1)

  val UP = Vector2(0, -1)
  val DOWN = Vector2(0, 1)
  val LEFT = Vector2(-1, 0)
  val RIGHT = Vector2(1, 0)
}