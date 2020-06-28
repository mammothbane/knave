package com.avaglir.knave.properties

import com.avaglir.knave.entities.Entity
import com.avaglir.knave.util._

class Fighter(
    parent: Entity,
    val maxHealth: Int = 10,
    val accuracy: UnitClampedFloat = 0.75f,
  ) extends Property[Entity](parent) {

  import Fighter._

  var curHealth: Int = maxHealth

  override def name: String = "fighter"

  override def message[T](message: Message[T]): Any =
    message match {
      case Message(Symbol("stats"), _)            => Stats((curHealth, maxHealth), accuracy)
      case Message(Symbol("combat"), Some(enemy)) =>
      case _                                      =>
    }

}

object Fighter extends Random {
  case class Stats(health: (Int, Int), accuracy: UnitClampedFloat)

  def stats: Message[Nothing]                  = Message(Symbol("stats"), None)
  def combat(other: Fighter): Message[Fighter] = Message(Symbol("combat"), Some(other))
}
