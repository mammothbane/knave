package com.avaglir.knave.util

class ClampedFloat(v: Float, min: Float = 0, max: Float = 1) {
  lazy val value = v.clamp(min, max)
  override def toString: String = s"$value"
}

final case class UnitClampedFloat(v: Float) extends ClampedFloat(v)
