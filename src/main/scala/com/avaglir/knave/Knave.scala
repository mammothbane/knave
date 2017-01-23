package com.avaglir.knave

import com.avaglir.knave.gamemode.{GameMode, OverworldMode, Start}
import com.avaglir.knave.map.Nation
import com.avaglir.knave.util._
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
  private var currentMode: GameMode = Start

  def main(): Unit = {
    displays.foreach {
      case (sym: Symbol, disp: Display) => document.getElementById(s"knave-${sym.name}").appendChild(disp.container)
    }

    window.addEventListener("keydown", handleInput _)
    window.addEventListener("keypress", handleInput _)
    displays.values.foreach { _.clear() }
    currentMode.render()

    val canvas = document.createElement("canvas").asInstanceOf[Canvas]
    canvas.height = 512
    canvas.width = 512

    document.body.appendChild(canvas)

    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    val scale = 512

    def redraw(): Unit = {
      ctx.fillStyle = Color("#11517f").darker.hex
      ctx.fillRect(0, 0, 512, 512)

      Nation.all.zipWithIndex.foreach {
        case (nation, index) =>
          ctx.fillStyle = HSL(index.toFloat/Nation.all.size, 0.4f, 0.5f).hex

          nation.land.foreach { landmass =>
            landmass.tiles.map{ _ * scale }.foreach { tile =>
              ctx.fillRect(tile.x, tile.y, 1, 1)
            }
            //          val oldstyle = ctx.fillStyle
            //          ctx.fillStyle = Color.RED.hex
            //          val ctr = landmass.center * scale
            //          ctx.fillRect(ctr.x - 2, ctr.y - 2, 4, 4)
            //          ctx.fillStyle = oldstyle
          }
      }
    }

    redraw()

    implicit def double2Int(d: Double): Int = d.toInt
    var dirty = false

    canvas.addEventListener("mousemove", (evt: MouseEvent) => {
      val coords = Vector2(evt.pageX - canvas.offsetLeft, evt.pageY - canvas.offsetTop).as[Int]
      println(s"mouseover at $coords")

      Nation.which(coords) match {
        case Some(nation) =>
          redraw()
          dirty = true
          ctx.fillStyle = Color.RED.darker.hex
          nation.land.map { _.edge }.foreach { _.foreach { tile =>
            val tl = tile * scale
            ctx.fillRect(tl.x, tl.y, 1, 1)
          } }

        case None =>
          if (dirty) redraw()
          dirty = false
      }

    })
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

  private val modeMap = Map[Symbol, GameMode](
    'start -> Start,
    'overworld -> OverworldMode
  )

  import prickle._
  override def persist(): Map[Symbol, String] = Map(
    'rand_seed -> JSON.stringify(random.getSeed()),
    'rand_state -> JSON.stringify(random.getState()),
    'game_mode -> modeMap.find { _._2 == currentMode }.get.toString
  )

  override def restore(v: Map[Symbol, String]): Unit = {
    random.setSeed(JSON.parse(v('rand_seed)).asInstanceOf[Double])
    random.setState(JSON.parse(v('rand_state)).asInstanceOf[RNGState])
    currentMode = modeMap(Symbol(v('game_mode)))
  }

  override val key: Symbol = 'knave_root
}
