package com.avaglir.knave.util

import java.util.Date

import rot.RNG

trait Random {
  implicit private var _seed: RandomSeed = RandomSeed(new Date().getTime)
  implicit lazy val random: RNG          = seededRandom

  def seed: Double             = _seed.value
  def setSeed(v: Double): Unit = _seed = RandomSeed(v)
}
