package com.avaglir.knave.skill

import com.avaglir.knave.util._

trait SkillBased {
  def skillValue: Int
  def efficacyFunction: Int => UnitClampedFloat
  def skillRating: UnitClampedFloat = efficacyFunction(skillValue)
}
