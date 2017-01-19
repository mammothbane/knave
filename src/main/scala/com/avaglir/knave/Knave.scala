package com.avaglir.knave

import com.avaglir.knave.gamemode.{GameMode, Start}
import com.avaglir.knave.map.{Islands, Landmass, Nation}
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
    ctx.fillStyle = Color("#11517f").darker.hex
    ctx.fillRect(0, 0, 500, 500)

    val colors = List(Color.WHITE, Color.RED, Color.GREEN, Color.BLUE)

    Islands.all.zipWithIndex.foreach {
      case (island, index) =>
        ctx.fillStyle = HSL(index.toFloat/Islands.all.length, 0.5f, 0.5f).hex

        island.foreach { tile =>
          ctx.fillRect(tile.x * 2, tile.y * 2, 2, 2)
        }
    }

//    implicit val rng = random
//
//    val hist = (0 until 20).map { _ =>
//      poisson(1) + 1
//    }.groupBy { identity }
//
//    hist.toList.sortBy { _._1 }.foreach { case (i, elems) => println(s"$i: ${elems.length}")}

//    val total = Islands.all.map { _.size }.sum - Islands.all.map { _.size }.max
//    Islands.all.map { _.size.toFloat / total * 100 }.foreach(println)

//    val (continent, subcontinent) = Islands.all.partition { _.size < (Landmass.CONTINENT_THRESHOLD*total) }

    println(s"${Landmass.all.size} landmasses; ${Nation.all.size} nations")

    Nation.all.toList.sortBy { -_.land.map { _.area }.sum }.foreach { nation =>
      println(s"${nation.nClass}: ${nation.land.size} islands of classes ${nation.land.toList.map { _.sizeClass.name }} (total land area: ${nation.land.map { _.area}.sum})")
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
