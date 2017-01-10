package com.avaglir.knave

import com.avaglir.knave.gamemode.{GameMode, Start}
import com.avaglir.knave.util._
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

    storage.loadAll()

    window.addEventListener("keydown", handleInput _)
    window.addEventListener("keypress", handleInput _)
    currentMode.render()
  }

  def handleInput(evt: KeyboardEvent): Unit = {
    if (evt.keyCode == 0) return // ignore modifier-only keypresses
    currentMode frame evt match {
      case Some(newMode) => {
        currentMode.exit()
        currentMode = newMode
      }
      case None =>
    }

    displays.values.foreach { _.clear() }
    currentMode.render()
  }
}
