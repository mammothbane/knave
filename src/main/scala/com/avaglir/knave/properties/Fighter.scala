package com.avaglir.knave.properties

import com.avaglir.knave.entities.Entity
import com.avaglir.knave.util._

class Fighter(
                 parent: Entity,
                 val maxHealth: Int = 10,
                 val accuracy: UnitClampedFloat = 0.75f
             ) extends Property[Entity](parent) {

    import Fighter._

    var curHealth = maxHealth

    override def name: String = "fighter"

    override def message[T](message: Message[T]): Any = message match {
        case Message('stats, _) => Stats((curHealth, maxHealth), accuracy)
        case Message('combat, Some(enemy)) =>
        case _ =>
    }
}

object Fighter extends Random {
    case class Stats(health: (Int, Int), accuracy: UnitClampedFloat)

    def stats = Message('stats, None)
    def combat(other: Fighter) = Message('combat, Some(other))
}