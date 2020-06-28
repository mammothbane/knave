package com.avaglir.knave

import com.avaglir.knave.util._

package object items {

  def polynomialEfficacy(power: Int)(floor: Int, ceiling: Int): (Int) => UnitClampedFloat = {
    val diff = floor - ceiling

    skill: Int => {
      val adjSkill: UnitClampedFloat = (skill - floor).clamp(0, diff).toFloat / diff
      var out = 1f
      for (_ <- 0 until power) {
        out *= adjSkill
      }
      out
    }
  }

  val linearEfficacy: (Int, Int) => Int => UnitClampedFloat = polynomialEfficacy(1)(_, _)
  val quadraticEfficacy: (Int, Int) => Int => UnitClampedFloat = polynomialEfficacy(2)(_, _)
}
