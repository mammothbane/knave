package com.avaglir.knave.skill

sealed trait Skill {
    def name: String
    def min: Int = 1
    def max: Int = 100
}

object Skill {
    def all = Set(ClothArmor, LeatherArmor, MailArmor, PlateArmor, StandardWeapon, GreatWeapon, RangedWeapon, Shields)

    case object ClothArmor extends Skill {
        val name = "cloth armor"
    }

    case object LeatherArmor extends Skill {
        val name = "leather armor";
        override val max = 150
    }

    case object MailArmor extends Skill {
        val name = "mail armor";
        override val max = 200
    }

    case object PlateArmor extends Skill {
        val name = "plate armor";
        override val max = 300
    }

    case object StandardWeapon extends Skill {
        val name = "standard weapons";
        override val max = 150
    }

    case object GreatWeapon extends Skill {
        val name = "great weapons";
        override val max = 350
    }

    case object RangedWeapon extends Skill {
        val name = "ranged weapons";
        override val max = 200
    }

    case object Shields extends Skill {
        val name = "shields"
    }
}