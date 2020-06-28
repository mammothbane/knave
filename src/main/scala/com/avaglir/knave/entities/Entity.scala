package com.avaglir.knave.entities

import com.avaglir.knave.properties.{Message, Property}
import com.avaglir.knave.util._

import scala.collection.mutable

trait Entity extends GameObject {
  var loc: Vector2[Int]

  private val props = mutable.Map.empty[String, Property[_]]

  def x: Int = loc.x
  def y: Int = loc.y

  def x_:(newX: Int): Unit = this.loc = Vector2(newX, y)
  def y_:(newY: Int): Unit = this.loc = Vector2(x, newY)

  final def register(p: Property[_]): Unit = props(p.name) = p

  def message[T, U](m: Message[T]): Map[String, Any] =
    props.view
      .mapValues(_.message(m))
      .filter { case (_, elem) => elem != () }
      .toMap
}
