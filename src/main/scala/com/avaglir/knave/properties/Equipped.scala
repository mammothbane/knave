package com.avaglir.knave.properties

import com.avaglir.knave.entities.Entity
import com.avaglir.knave.items.{Equippable, GearSlot}

import scala.collection.mutable

class Equipped(parent: Entity, slots: Set[_ <: GearSlot]) extends Property[Entity](parent) {
    import Equipped._

    val equipState = mutable.Map.empty[GearSlot, Equippable]

    override def name: String = "armored"

    override def message[T](message: Message[T]): Any = message match {
        case Message('equip, Some(Equip(slot, eq))) =>
            equipState.get(slot) match {
                case Some(x) =>
                    equipState(slot) = eq
                    x
                case None => equipState(slot) = eq
            }

        case Message('equipped, _) => equipState.toMap
        case _ =>
    }
}

object Equipped {
    case class Equip(gearSlot: GearSlot, equippable: Equippable)
    def equip(eq: Equippable) = Message('equip, Some(Equip(eq.slot, eq)))
    def equipped = Message('equipped, None)
}
