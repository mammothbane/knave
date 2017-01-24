package com.avaglir.knave.entities

import com.avaglir.knave.properties.{Message, Property}
import com.avaglir.knave.util._

import scala.collection.mutable

trait Entity extends GameObject {
  var loc: Vector2[Int]
  def x = loc.x
  def y = loc.y
  def x_:(newX: Int) = this.loc = Vector2(newX, y)
  def y_:(newY: Int) = this.loc = Vector2(x, newY)

  private val props = mutable.Map.empty[String, Property[_]]
  final def register(p: Property[_]) = props(p.name) = p
  def message[T, U](m: Message[T, U]) = props.values.foreach { _.message(m) }
}
