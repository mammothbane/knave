package com.avaglir.knave

import com.avaglir.knave.gamemode.{GameMode, Start}
import com.avaglir.knave.map.{Cellular, GameMap}
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

//    storage.loadAll()

    window.addEventListener("keydown", handleInput _)
    window.addEventListener("keypress", handleInput _)
    displays.values.foreach { _.clear() }
    currentMode.render()

    val tiles = Cellular.generate(50, 50, 0.6f)
    val map = GameMap(tiles)
    map.log()
  }

  def handleInput(evt: KeyboardEvent): Unit = {
    if (evt.keyCode == 0) return // ignore modifier-only keypresses
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
