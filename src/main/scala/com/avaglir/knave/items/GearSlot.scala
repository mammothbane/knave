package com.avaglir.knave.items

sealed trait GearSlot {
  def name: String
}

object GearSlot {
  case object Head extends GearSlot { val name = "head" }
  case object Torso extends GearSlot { val name = "torso" }
  case object Legs extends GearSlot { val name = "legs" }
  case object Boots extends GearSlot { val name = "boots" }
  case object Gloves extends GearSlot { val name = "gloves" }
  case object Weapon extends GearSlot { val name = "weapon" }
  case object Shield extends GearSlot { val name = "shield" }
}
