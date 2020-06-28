package com.avaglir.knave.util

case class Vector2[@specialized(Int, Double, Float) T: Numeric](x: T, y: T) {
    private val num = implicitly[Numeric[T]]
    import num._

    @inline def unary_-(): Vector2[T] = map(num.negate)

    @inline def +[V](other: Vector2[V])(implicit conv: V => T) = Vector2(x + other.x, y + other.y)
    @inline def -[V](other: Vector2[V])(implicit conv: V => T) = Vector2(x - other.x, y - other.y)
    @inline def *[V](factor: V)(implicit conv: V => T) = map { elem => num.times(elem, factor) }
    @inline def /[V](factor: V)(implicit conv: V => T): Vector2[T] = num match {
        case n: Integral[T] => map { elem => n.quot(elem, factor) }
        case n: Fractional[T] => map { elem => n.div(elem, factor) }
    }

    @inline def dot[V](other: Vector2[V])(implicit conv: V => T) = x * other.x + y * other.y

    @inline def half = this / (one + one)

    @inline def magnitude: Double = {
        val doubleX = x.toDouble
        val doubleY = y.toDouble

        math.sqrt(doubleX * doubleX + doubleY * doubleY)
    }

    @inline def normalize: Vector2[Double] = map {
        _.toDouble
    } / magnitude

    @inline def transpose = Vector2(y, x)

    @inline def map[V: Numeric](fn: (T) => V): Vector2[V] = Vector2(fn(x), fn(y))

    @inline def as[V: Numeric](implicit conv: T => V): Vector2[V] = map(conv)

    def adjacent: List[Vector2[T]] = adjacent(false)

    def adjacent(diag: Boolean): List[Vector2[T]] = List(
        this + Vector2.UP,
        this + Vector2.DOWN,
        this + Vector2.LEFT,
        this + Vector2.RIGHT
    ) ++ (if (diag) List(
        this + Vector2.UP + Vector2.RIGHT,
        this + Vector2.DOWN + Vector2.RIGHT,
        this + Vector2.UP + Vector2.LEFT,
        this + Vector2.DOWN + Vector2.LEFT
    ) else List.empty)

    @inline def componentsClamped[V](max: Vector2[V])(implicit conv: V => T, convU: Int => T): Boolean = componentsClamped(max, Vector2.ZERO)
    @inline def componentsClamped[V, U](max: Vector2[V], min: Vector2[U])(implicit conv: V => T, convU: U => T): Boolean = this >= min && this < max

    override def toString: String = s"v2($x, $y)"

    @inline def <=[V](other: Vector2[V])(implicit conv: V => T): Boolean = x <= other.x && y <= other.y
    @inline def <[V](other: Vector2[V])(implicit conv: V => T): Boolean = x < other.x && y < other.y
    @inline def >=[V](other: Vector2[V])(implicit conv: V => T): Boolean = x >= other.x && y >= other.y
    @inline def >[V](other: Vector2[V])(implicit conv: V => T): Boolean = x > other.x && y > other.y

    @inline def octant: Int = {
        if (x > zero && y >= zero) { // first quadrant
            if (x >= y) 1
            else 2
        } else if (y > zero && x <= zero) { // second quadrant
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

    @inline def clamp[V, W](min: Vector2[V], max: Vector2[W])(implicit convV: V => T, convW: W => T): Vector2[T] =
        Vector2(minOf[T](max.x, maxOf[T](min.x, x)), minOf[T](max.y, maxOf[T](min.y, y)))
}

object Vector2 {
    def apply[T: Numeric](t: (T, T)): Vector2[T] = Vector2(t._1, t._2)

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
        Vector2(zero, one)
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