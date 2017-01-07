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

    val colorList = List(Color.GREEN, Color.RED, Color.BLUE)
    val max = 15

    for (i <- 1 to max) {
      midpoint(Vector2(40, 12), i).foreach((vec) => {
        val color = colorList(i % colorList.length)
        val hsl = HSL(i.toFloat / max, color.saturation, color.luminance * (max - i)/max)

        displays('main).draw(vec, 'a', hsl)
      })
    }
  }
}
