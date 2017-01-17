package com.avaglir.knave.entities

import com.avaglir.knave.util._

trait Entity extends GameObject {
  var loc: Vector2[Int]
  def x = loc.x
  def y = loc.y
  def x_:(newX: Int) = this.loc = Vector2(newX, y)
  def y_:(newY: Int) = this.loc = Vector2(x, newY)
}
