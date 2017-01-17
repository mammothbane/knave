package com.avaglir.knave.util

case class Vector2[T: Numeric](x: T, y: T) {
  private lazy val num = implicitly[Numeric[T]]
  import num._

  def +[V](other: Vector2[V])(implicit conv: V => T) = Vector2(x + other.x, y + other.y)
  def -[V](other: Vector2[V])(implicit conv: V => T) = Vector2(x - other.x, y - other.y)
  def dot[V](other: Vector2[V])(implicit conv: V => T) = x * other.x + y * other.y

  def unary_-(): Vector2[T] = map(num.negate)

  def *[V](factor: V)(implicit conv: V => T) = map { elem => num.times(elem, factor) }
  def /[V](factor: V)(implicit conv: V => T): Vector2[T] = num match {
    case n: Integral[T] => map { elem => n.quot(elem, factor) }
    case n: Fractional[T] => map { elem => n.div(elem, factor) }
  }

  def half = this / (one + one)
  def magnitude: Double = {
    val doubleX = x.toDouble
    val doubleY = y.toDouble

    math.sqrt(doubleX*doubleX + doubleY*doubleY)
  }
  def normalize: Vector2[Double] = map { _.toDouble } / magnitude
  def transpose = Vector2(y, x)

  def map[V: Numeric](fn: (T) => V): Vector2[V] = Vector2(fn(x), fn(y))
  def asVec[V: Numeric](implicit conv: T => V): Vector2[V] = map(conv)

  def adjacent: Set[Vector2[T]] = adjacent(false)
  def adjacent(diag: Boolean): Set[Vector2[T]] = Set(
    this + Vector2.UP,
    this + Vector2.DOWN,
    this + Vector2.LEFT,
    this + Vector2.RIGHT
  ) ++ (if (diag) Set(
    this + Vector2.UP + Vector2.RIGHT,
    this + Vector2.DOWN + Vector2.RIGHT,
    this + Vector2.UP + Vector2.LEFT,
    this + Vector2.DOWN + Vector2.LEFT
  ) else Set.empty)

  def componentsClamped[V](max: Vector2[V])(implicit conv: V => T, convU: Int => T): Boolean = componentsClamped(max, Vector2.ZERO)
  def componentsClamped[V, U](max: Vector2[V], min: Vector2[U])(implicit conv: V => T, convU: U => T): Boolean = this >= min && this < max

  override def toString: String = s"v2($x, $y)"

  def <=[V](other: Vector2[V])(implicit conv: V => T): Boolean = this == other || this < other
  def <[V](other: Vector2[V])(implicit conv: V => T): Boolean = x < other.x && y < other.y
  def >=[V](other: Vector2[V])(implicit conv: V => T): Boolean = this == other || this > other
  def >[V](other: Vector2[V])(implicit conv: V => T): Boolean = x > other.y && y > other.y

  def octant: Int = {
    if (x > zero && y >= zero) { // first quadrant
      if (x >= y) 1
      else 2
    } else if (y > zero && x <= zero) {  // second quadrant
      if (-x >= y) 4
      else 3
    } else if (y <= zero && x < zero) { // third quadrant
      if (-x >= -y) 5
      else 6
    } else if (y < zero && x >= zero) { // fourth quadrant
      if (x >= -y) 8
      else 7
    } else 0 // origin
  }
}

object Vector2 {
  def ZERO[T: Numeric]: Vector2[T] = {
    val num = implicitly[Numeric[T]]
    import num._
    Vector2(zero, zero)
  }
  def UNIT[T: Numeric]: Vector2[T] = {
    val num = implicitly[Numeric[T]]
    import num._
    Vector2(one, one)
  }

  def UP[T: Numeric]: Vector2[T] = {
    val num = implicitly[Numeric[T]]
    import num._
    Vector2(zero, -one)
  }
  def DOWN[T: Numeric]: Vector2[T] = {
    val num = implicitly[Numeric[T]]
    import num._
    Vector2(zero, -one)
  }
  def LEFT[T: Numeric]: Vector2[T] = {
    val num = implicitly[Numeric[T]]
    import num._
    Vector2(-one, zero)
  }
  def RIGHT[T: Numeric]: Vector2[T] = {
    val num = implicitly[Numeric[T]]
    import num._
    Vector2(one, zero)
  }
}