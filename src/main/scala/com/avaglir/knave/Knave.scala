package com.avaglir.knave

import com.avaglir.knave.gamemode.{GameMode, Start}
import com.avaglir.knave.util._
import org.scalajs.dom.document

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
      case (sym: Symbol, disp: Display) =>
        document.getElementById(s"knave-${sym.name}").appendChild(disp.container)
    }

    var last = Vector2.ZERO
    bresenhamLine(Vector2.ZERO, Vector2(displays('main).width, displays('main).height) - Vector2.UNIT).foreach(vec => {
      displays('main).draw(vec, 'a', Color.WHITE)
      last = vec
    })

    println(last)
  }
}
