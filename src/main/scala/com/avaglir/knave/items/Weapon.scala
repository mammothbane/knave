package com.avaglir.knave.items

import com.avaglir.knave.entities.Entity
import com.avaglir.knave.items.Armor.ArmorType
import com.avaglir.knave.properties.Skilled
import com.avaglir.knave.skill.{Skill, SkillBased}
import com.avaglir.knave.util._

abstract class Weapon(owner: Entity) extends Item(owner) with SkillBased {
  import Weapon._

  def baseDamage: Int
  def baseHit: UnitClampedFloat
  def endurance: Int
  def weaponClass: WeaponClass

  def great: Boolean = false

  override def skillValue: Int = {
    val skill = weaponClass match {
      case WeaponClass.Bow => Skill.RangedWeapon
      case _ if great => Skill.GreatWeapon
      case _ => Skill.StandardWeapon
    }

    var out: Option[Int] = None
    owner.message(Skilled.skillValue(skill, Some((ret) => out = Some(ret))))
    out match {
      case Some(elem) => elem
      case _ => skill.min
    }
  }
}

object Weapon {
  sealed trait WeaponClass {
    def name: String
    def stdRange: Int = 1
    def stdDamage: Int = 1
    def dType: DamageType
  }

  object WeaponClass {
    val all = Set(Sword, Spear, Axe, Trident, Bow, Dagger, Club)

    case object Sword extends WeaponClass { val name = "sword"; val dType = DamageType.Slash }
    case object Spear extends WeaponClass { val name = "spear"; override val stdRange = 2; val dType = DamageType.Pierce }
    case object Axe extends WeaponClass { val name = "axe"; override val stdDamage = 2; val dType = DamageType.Crush }
    case object Trident extends WeaponClass { val name = "trident"; override val stdRange = 2; val dType = DamageType.Pierce }
    case object Bow extends WeaponClass { val name = "bow"; override val stdRange = 10; val dType = DamageType.Pierce }
    case object Dagger extends WeaponClass { val name = "dagger"; val dType = DamageType.Pierce }
    case object Club extends WeaponClass { val name = "club"; val dType = DamageType.Crush }
  }

  sealed trait DamageType {
    def name: String
    def strengths: Set[ArmorType]
  }

  object DamageType {
    val all = Set(Pierce, Crush, Slash)

    case object Pierce extends DamageType { val name = "piercing"; val strengths = Set(ArmorType.Leather, ArmorType.Mail) }
    case object Crush extends DamageType { val name = "crush"; val strengths = Set(ArmorType.Plate) }
    case object Slash extends DamageType { val name = "slash"; val strengths = Set(ArmorType.Cloth, ArmorType.Leather) }
  }
}
