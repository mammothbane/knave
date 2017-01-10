package com.avaglir.knave.util.random

import java.util.Date

case class Alea(var state: AleaState) {
  def random: Double = {
    val t = 2091639 * state.s0 + state.c * 2.3283064365386963e-10
    val s0 = state.s1
    val s1 = state.s2
    val c = t.toInt
    val s2 = t.toDouble - state.c
    state = AleaState(s0, s1, s2, c)

    s2
  }

  def randInt: Int = (random * Int.MaxValue).toInt
  def frac53: Double = random + math.floor(random * 0x200000) * 1.1102230246251565e-16
}

final case class AleaState private (private[random] val s0: Double,
                                    private[random] val s1: Double,
                                    private[random] val s2: Double,
                                    private[random] val c: Double)

object Alea {
  private class Mash {
    var n: Double = 0xefc8249d
    def mash(s: String): Double = {
      s.foreach { c =>
        n += c
        var h: Double = 0.02519603282416938 * n
        n = h.toInt >>> 0
        h -= n
        h *= n
        n = h.toInt >>> 0
        h -= n
        n += h * Int.MaxValue
      }
      (n.toInt >>> 0) * 2.3283064365386963e-10
    }
  }

  def apply(arg: String = ""): Alea = {
    val m = new Mash

    var states = List(m.mash(" "), m.mash(" "), m.mash(" "))

    def update(arg: Double) = {
      states = states.map { state =>
        val n = state - m.mash(arg.toString)
        if (n < 0) n + 1 else n
      }
    }

    (if (arg.isEmpty) {
      List(new Date().getTime)
    } else {
      arg.map {_.toLong}
    }).foreach { elem => update(elem.toDouble) }

    Alea(AleaState(states.head, states(1), states(2), 1))
  }
}
