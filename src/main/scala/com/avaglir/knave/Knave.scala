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

    midpoint(Vector2(40, 12), 5).foreach((vec) => {
      displays('main).draw(vec, 'a')
    })

    midpoint(Vector2(40, 12), 4).foreach((vec) => {
      displays('main).draw(vec, 'a', Color.BLUE)
    })

    midpoint(Vector2(40, 12), 3).foreach((vec) => {
      displays('main).draw(vec, 'a', Color.RED)
    })

    midpoint(Vector2(40, 12), 6).foreach((vec) => {
      displays('main).draw(vec, 'a', Color.GREEN)
    })

    midpoint(Vector2(40, 12), 2).foreach((vec) => {
      displays('main).draw(vec, 'a', Color.GREEN)
    })

    midpoint(Vector2(40, 12), 1).foreach((vec) => {
      displays('main).draw(vec, 'a', Color.BLUE)
    })

    midpoint(Vector2(40, 12), 8).foreach((vec) => {
      displays('main).draw(vec, 'a', Color.RED)
    })

    midpoint(Vector2(40, 12), 7).foreach((vec) => {
      displays('main).draw(vec, 'a', Color.BLUE)
    })
  }
}
