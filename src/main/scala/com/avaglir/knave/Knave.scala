package com.avaglir.knave

import com.avaglir.knave.gamemode.{GameMode, Start}
import com.avaglir.knave.map.Islands
import com.avaglir.knave.util._
import com.avaglir.knave.util.storage.Pickling._
import org.scalajs.dom._
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.html.Canvas
import rot.RNGState

import scala.scalajs.js.{JSApp, JSON}

object Knave extends JSApp with Persist {
  val displays = Map(
    'main -> new Display(80, 24),
    'status -> new Display(20, 24),
    'messages -> new Display(101, 6)
  )

  val random = rot.RNG
  private var currentMode: GameMode = Start()

  def main(): Unit = {
    displays.foreach {
      case (sym: Symbol, disp: Display) => document.getElementById(s"knave-${sym.name}").appendChild(disp.container)
    }

    window.addEventListener("keydown", handleInput _)
    window.addEventListener("keypress", handleInput _)
    displays.values.foreach { _.clear() }
    //    currentMode.render()

    val canvas = document.createElement("canvas").asInstanceOf[Canvas]
    canvas.height = 500
    canvas.width = 500

    document.body.appendChild(canvas)

    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    ctx.fillStyle = Color.BLACK.hex
    ctx.fillRect(0, 0, 500, 500)

    val colors = List(Color.WHITE, Color.RED, Color.GREEN, Color.BLUE)

//    val out = GenMap.emit(500)
//    ctx.fillStyle = Color.WHITE.hex
//    for (x <- out.indices; y <- out.indices) {
//      if (out(x)(y) > 0.35) ctx.fillRect(x, y, 1, 1)
//    }
//
//    println(Islands.all.head.size)
//    Islands.all.foreach(println)

    Islands.edges.foreach { island =>
      ctx.fillStyle = colors(random.int(0, 3)).hex
      island.foreach { tile =>
        ctx.fillRect(tile.x, tile.y, 1, 1)
      }
    }

//    println("generating map")
//    val out = GenMap.emit(500)
//
//    println("rendering map")
//    for (i <- 0 until 500; j <- 0 until 500) {
////      ctx.fillStyle = HSL(0f, 0f, out(i)(j)).hex
//      ctx.fillStyle = if (out(i)(j) > 0.35) Color.WHITE.hex else Color.BLACK.hex
//      ctx.fillRect(i, j, 1, 1)
//    }
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

  import prickle._
  override def persist(): Map[Symbol, String] = Map(
    'rand_seed -> JSON.stringify(random.getSeed()),
    'rand_state -> JSON.stringify(random.getState()),
    'game_mode -> Pickle.intoString(currentMode)
  )

  override def restore(v: Map[Symbol, String]): Unit = {
    random.setSeed(JSON.parse(v('rand_seed)).asInstanceOf[Double])
    random.setState(JSON.parse(v('rand_state)).asInstanceOf[RNGState])
    currentMode = Unpickle[GameMode].fromString(v('game_mode)).get
  }

  override val key: Symbol = 'knave_root
}
