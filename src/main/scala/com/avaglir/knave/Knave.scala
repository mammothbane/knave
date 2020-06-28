package com.avaglir.knave

import com.avaglir.knave.entities.Player
import com.avaglir.knave.gamemode.{GameMode, OverworldMode, Start}
import com.avaglir.knave.map.{Chunk, Nation, NationClass, Overworld}
import com.avaglir.knave.util._
import org.scalajs.dom._
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.html.Canvas

object Knave extends Random {

  val displays = Map(
    Symbol("main")     -> new Display(80, 24),
    Symbol("status")   -> new Display(20, 24),
    Symbol("messages") -> new Display(101, 6),
  )

  private var currentMode: GameMode = Start

  def main(args: Array[String]): Unit = {
    displays.foreach {
      case (sym: Symbol, disp: Display) =>
        document.getElementById(s"knave-${sym.name}").appendChild(disp.container)
    }

    val target = Nation.all.filter { nation =>
      nation.nClass == NationClass.Barony || nation.nClass == NationClass.County
    }.head.land.head.center * Chunk.DIMENS * 8
    Player.loc = target

    window.addEventListener("keydown", handleInput _)
    window.addEventListener("keypress", handleInput _)

    displays.values.foreach {
      _.clear()
    }
    currentMode.render()

    val nationText = document.getElementById("nation-label")
    val canvas     = document.getElementById("map").asInstanceOf[Canvas]

    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    def redraw(): Unit = {
      while (nationText.firstChild != null) {
        nationText.removeChild(nationText.firstChild)
      }
      ctx.fillStyle = Color("#11517f").darker.hex
      ctx.fillRect(0, 0, canvas.width, canvas.height)

      Nation.all.zipWithIndex.foreach {
        case (nation, index) =>
          ctx.fillStyle = HSL(index.toFloat / Nation.all.size, 0.4f, 0.5f).hex

          nation.land.foreach { landmass =>
            landmass.tiles.foreach { tile =>
              ctx.fillRect(tile.x, tile.y, 1, 1)
            }
          }
      }

      ctx.fillStyle = Color.RED.hex
      ctx.fillRect(Player.x / Chunk.DIMENS / 8, Player.y / Chunk.DIMENS / 8, 4, 4)
    }

    redraw()

    implicit def double2Int(d: Double): Int = d.toInt

    var dirty = false

    canvas.addEventListener("mouseleave", (_: MouseEvent) => redraw())
    canvas.addEventListener(
      "mousemove",
      (evt: MouseEvent) => {
        val coords = Vector2(evt.pageX - canvas.offsetLeft, evt.pageY - canvas.offsetTop).as[Int]

        Nation.which(coords) match {
          case Some(nation) =>
            redraw()
            dirty = true
            ctx.fillStyle = Color.RED.hex
            nation.land.map {
              _.edge
            }.foreach {
              _.foreach { tile =>
                ctx.fillRect(tile.x, tile.y, 1, 1)
              }
            }

            val islandtext = nation.land.map {
              _.print
            }
            val tn = document.createTextNode(s"${nation.print}\n")
            val b  = document.createElement("b")
            b.appendChild(tn)
            nationText.appendChild(b)

            islandtext.foreach { tx =>
              nationText.appendChild(document.createElement("br"))
              val i = document.createElement("i")
              i.appendChild(document.createTextNode(tx))
              nationText.appendChild(i)
            }

          case None =>
            if (dirty) redraw()
            dirty = false
        }

      },
    )

    canvas.addEventListener(
      "click",
      (evt: MouseEvent) => {
        val coords = Vector2(evt.pageX - canvas.offsetLeft, evt.pageY - canvas.offsetTop).as[Int]

        if (currentMode == OverworldMode) {
          Player.loc = coords * Chunk.DIMENS * 8
          Overworld.setCenter(Player.loc)

          displays.values.foreach {
            _.clear()
          }
          OverworldMode.render()
        }
        redraw()
      },
    )
  }

  final val ignoreKeyCodes = Set(
    KeyCode.Alt,
    KeyCode.Tab,
    KeyCode.Ctrl,
    KeyCode.Home,
    KeyCode.PageUp,
    KeyCode.PageDown,
    KeyCode.Shift,
    KeyCode.End,
    KeyCode.Insert,
    KeyCode.Pause,
    KeyCode.F1,
    KeyCode.F2,
    KeyCode.F3,
    KeyCode.F4,
    KeyCode.F5,
    KeyCode.F6,
    KeyCode.F7,
    KeyCode.F8,
    KeyCode.F9,
    KeyCode.F10,
    KeyCode.F11,
    KeyCode.F12,
  )

  def handleInput(evt: KeyboardEvent): Unit = {
    if (ignoreKeyCodes contains evt.keyCode) return

    currentMode.frame(evt) match {
      case Some(newMode) =>
        currentMode.exit()
        currentMode = newMode
        currentMode.enter()
      case None =>
    }

    displays.values.foreach {
      _.clear()
    }
    currentMode.render()
  }

  private val modeMap = Map[Symbol, GameMode](
    Symbol("start")     -> Start,
    Symbol("overworld") -> OverworldMode,
  )
}
