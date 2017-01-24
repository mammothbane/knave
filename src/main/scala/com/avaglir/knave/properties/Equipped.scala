package com.avaglir.knave.properties

import com.avaglir.knave.entities.Entity
import com.avaglir.knave.items.{Equippable, GearSlot}

import scala.collection.mutable

class Equipped(parent: Entity, slots: Set[GearSlot]) extends Property[Entity](parent) {
  import Equipped._

  val equipState = mutable.Map.empty[GearSlot, Equippable]

  override def name: String = "armored"
  override def message[T, U](message: Message[T, U]): Unit = message match {
    case Message('equip, Some(Equip(slot, eq)), elem) =>
      val cur = equipState(slot)
      equipState(slot) = eq
      if (elem.isDefined) elem.asInstanceOf[Option[Equippable] => Unit].apply(Some(cur))
    case _ =>
  }
}

object Equipped {
  case class Equip(gearSlot: GearSlot, equippable: Equippable)
  def equip(eq: Equippable, cb: Option[(Option[Equippable]) => Unit]) = Message('equip, Some(Equip(eq.slot, eq)), cb)
}
