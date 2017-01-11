package com.avaglir.knave

import com.avaglir.knave.gamemode.{GameMode, Start}
import com.avaglir.knave.util._
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.{KeyboardEvent, document, window}

import scala.scalajs.js.JSApp

object Knave extends JSApp {
  val displays = Map(
    'main -> new Display(80, 24),
    'status -> new Display(20, 24),
    'messages -> new Display(101, 6)
  )

  val random = rot.RNG
  private var currentMode: GameMode = new Start()
  private var inFrame = false

  def main(): Unit = {
    displays.foreach {
      case (sym: Symbol, disp: Display) => document.getElementById(s"knave-${sym.name}").appendChild(disp.container)
    }

//    storage.loadAll()

    window.addEventListener("keydown", handleInput _)
    window.addEventListener("keypress", handleInput _)
    displays.values.foreach { _.clear() }
    //    currentMode.render()

    //    val tiles = Simplex.generate(50, 50, 0.3)
    //    val map = GameMap(tiles)

    val scale = 8
    val scaleSmall = 3
    val smallWeight = 0.4
    val largeWeight = 0.75


    val main = displays('main)

    val large = simplex.SimplexNoise(random.int(Int.MinValue, Int.MaxValue))
    val small = simplex.SimplexNoise(random.int(Int.MinValue, Int.MaxValue))

    (0 until main.width).foreach { x =>
      (0 until main.height).foreach { y =>
        val vec = Vector2(x, y)

        val v = large.eval(x.toFloat / scale, y.toFloat / scale) * largeWeight.toFloat +  small.eval(vec.x.toFloat / scaleSmall, vec.y.toFloat / scaleSmall) * smallWeight.toFloat
        displays('main).draw(vec, 'a', HSL(0f, 0f, v.toFloat))
      }
    }
  }

  final val ignoreKeyCodes = Set(
    KeyCode.Alt, KeyCode.Tab, KeyCode.Ctrl, KeyCode.Home, KeyCode.PageUp, KeyCode.PageDown,
    KeyCode.Shift, KeyCode.End, KeyCode.Insert, KeyCode.Pause,
    KeyCode.F1, KeyCode.F2, KeyCode.F3, KeyCode.F4, KeyCode.F5, KeyCode.F6,
    KeyCode.F7, KeyCode.F8, KeyCode.F9, KeyCode.F10, KeyCode.F11, KeyCode.F12
  )

  def handleInput(evt: KeyboardEvent): Unit = {
    if (ignoreKeyCodes contains evt.keyCode) return

    currentMode frame evt match {
      case Some(newMode) =>
        currentMode.exit()
        currentMode = newMode
      case None =>
    }

    displays.values.foreach { _.clear() }
    currentMode.render()
  }
}
