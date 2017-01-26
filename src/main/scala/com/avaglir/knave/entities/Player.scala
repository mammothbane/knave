package com.avaglir.knave.entities

import com.avaglir.knave.items
import com.avaglir.knave.items.Armor.ArmorType
import com.avaglir.knave.items.Weapon.WeaponClass
import com.avaglir.knave.items.{Armor, GearSlot, Weapon}
import com.avaglir.knave.properties._
import com.avaglir.knave.util._

object Player extends Entity {
  val repr = RenderTile('@', HSL(Color.YELLOW.hue, 0.67f, 0.7f))
  override var loc: Vector2[Int] = Vector2.ZERO[Int]

  register(new Equipped(this, GearSlot.all))
  register(new Skilled(this))
  register(new Fighter(this))

  message(Equipped.equip(new Weapon(this) {
    override val name = "test axe"
    override val endurance = 32
    override val baseDamage = 3
    override val weaponClass = WeaponClass.Axe
    override val baseHit = 0.8f
    override val efficacyFunction = items.linearEfficacy(0, 100)
    override val basePrice = 30
    override val repr = RenderTile('x')
  }))

  message(Equipped.equip(new Armor(this) {
    override val name: String = "test robe"
    override val armorType: ArmorType = ArmorType.Cloth
    override val slot: GearSlot = GearSlot.Torso
    override val baseArmor: Int = 1
    override val baseEvasion: Int = 5
    override val efficacyFunction: (Int) => UnitClampedFloat = items.quadraticEfficacy(0, 30)
    override val basePrice: Int = 40
    override val repr: RenderTile = RenderTile('r')
  }))
}
