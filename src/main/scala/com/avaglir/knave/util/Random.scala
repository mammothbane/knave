package com.avaglir.knave.util

import java.util.Date

trait Random {
    private implicit var _seed = RandomSeed(new Date().getTime)
    implicit lazy val random = seededRandom

    def seed = _seed.value
    def setSeed(v: Double) = _seed = RandomSeed(v)
}
