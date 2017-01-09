package com.avaglir.knave

import com.avaglir.knave.gamemode.{GameMode, Start}
import com.avaglir.knave.util._
import org.scalajs.dom.document

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.util.Random

object Knave extends JSApp {
  val displays = Map(
    'main -> new Display(80, 24),
    'status -> new Display(20, 24),
    'messages -> new Display(101, 6)
  )

  val random = new Random()

  private var currentMode: GameMode = new Start()

  def main(): Unit = {
    displays.foreach {
      case (sym: Symbol, disp: Display) => document.getElementById(s"knave-${sym.name}").appendChild(disp.container)
    }

    val center = Vector2(40, 12)
    val fov = 15

    var i = 0

    js.timers.setInterval(500f) {
      val newVec = Vector2(center.x + (i % (2 * fov)) - fov, center.y)
      val visible = ShadowRaycast.calculate(center, fov, v => !v.equals(newVec)).toSet
      val nonVisible = circle_simple(center, fov).toSet diff visible

      visible.foreach { vec =>
        displays('main).draw(vec, 'a')
      }

      val dimmed = HSL(0f, 0f, 0.5f)
      nonVisible.foreach { vec => displays('main).draw(vec, 'a', dimmed) }
      i += 1
    }
  }
}
