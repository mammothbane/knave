package com.avaglir.knave

import com.avaglir.knave.gamemode.{GameMode, Start}
import com.avaglir.knave.util._
import org.scalajs.dom.document

import scala.scalajs.js
import scala.scalajs.js.JSApp

object Knave extends JSApp {
  val displays = Map(
    'main -> new Display(80, 24),
    'status -> new Display(20, 24),
    'messages -> new Display(101, 6)
  )

  private var currentMode: GameMode = new Start()

  def main(): Unit = {
    displays.foreach {
      case (sym: Symbol, disp: Display) => document.getElementById(s"knave-${sym.name}").appendChild(disp.container)
    }

    val center = Vector2(40, 12)
    val lines = List(
      bresenhamLine(center, center + Vector2.UP * 4),
      bresenhamLine(center, center + Vector2.DOWN * 4),
      bresenhamLine(center, center + Vector2.RIGHT * 4),
      bresenhamLine(center, center + Vector2.LEFT * 4),
      bresenhamLine(center, center + Vector2.UNIT.normalize * 4),
      bresenhamLine(center, center + Vector2.UNIT.normalize * -4),
      bresenhamLine(center, center + Vector2(1, -1).normalize * 4),
      bresenhamLine(center, center + Vector2(1, -1).normalize * -4)
    )

    lines.foreach { line => line.foreach { vec =>
      displays('main).draw(vec, 'a')
    } }

    val fov = 7

    var i = 0

//    js.timers.setInterval(500f) {
//      val newVec = Vector2(center.x + (i % (2*fov)) - fov, center.y)
//      println(newVec)
//
//      val visible = ShadowRaycast.calculate(center, fov, v => !v.equals(newVec)).toSet
//      val nonVisible = circle_simple(center, fov).toSet diff visible
//
//      println(s"${nonVisible.size} occluded squares, ${visible.size} visible squares")
//
//      visible.foreach { vec =>
//        displays('main).draw(vec, 'a')
//      }
//
//      val dimmed = HSL(0f, 0f, 0.5f)
//      nonVisible.foreach { vec => displays('main).draw(vec, 'a', dimmed) }
//      i += 1
//    }

  }
}
