package com.avaglir.knave

import org.scalajs.dom.document
import rot.Display

import scala.scalajs.js
import scala.scalajs.js.JSApp

object Knave extends JSApp {
  val displays = Map(
    'main -> new Display(js.Dynamic.literal(width = 80, height = 24)),
    'status -> new Display(js.Dynamic.literal(width = 20, height = 24)),
    'messages -> new Display(js.Dynamic.literal(width = 101, height = 6))
  )

  def main(): Unit = {
    displays.foreach {
      case (sym: Symbol, disp: Display) =>
        document.getElementById(s"knave-${sym.name}").appendChild(disp.container())
    }
  }
}
