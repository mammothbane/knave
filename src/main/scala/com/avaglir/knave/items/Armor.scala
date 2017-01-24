package com.avaglir.knave.items

import com.avaglir.knave.entities.Entity
import com.avaglir.knave.items.Armor.ArmorType._
import com.avaglir.knave.items.Weapon.DamageType
import com.avaglir.knave.properties.Skilled
import com.avaglir.knave.skill.{Skill, SkillBased}

abstract class Armor(owner: Entity) extends Item(owner) with SkillBased {
  import Armor._

  def slot: GearSlot
  def armorType: ArmorType
  def baseArmor: Int
  def baseEvasion: Int

  override def skillValue: Int = {
    val skill = armorType match {
      case Cloth => Skill.ClothArmor
      case Leather => Skill.LeatherArmor
      case Mail => Skill.MailArmor
      case Plate => Skill.PlateArmor
    }
    var out: Option[Int] = None
    owner.message(Skilled.skillValue(skill, Some((ret) => out = Some(ret))))
    out match {
      case Some(elem) => elem
      case _ => skill.min
    }
  }
}

object Armor {
  sealed trait ArmorType {
    def name: String
    def stdProtection: Int
    lazy val weaknesses = DamageType.all.filter { _.strengths contains this }
  }

  object ArmorType {
    case object Cloth extends ArmorType { val name = "cloth"; val stdProtection = 1 }
    case object Leather extends ArmorType { val name = "leather"; val stdProtection = 4 }
    case object Mail extends ArmorType { val name = "mail"; val stdProtection = 7 }
    case object Plate extends ArmorType { val name = "plate"; val stdProtection = 11 }
  }
}
