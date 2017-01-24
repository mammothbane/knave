package com.avaglir.knave.properties

import com.avaglir.knave.entities.Entity
import com.avaglir.knave.items.{GearSlot, Item}

class Equipped(parent: Entity, slots: Set[GearSlot]) extends Property[Entity](parent) {
  val equipState = Map[GearSlot, Item]

  override def name: String = "armored"
  override def message[U](message: Message[U]): Unit = message match {

    case _ =>
  }
}

object Equipped {

}
