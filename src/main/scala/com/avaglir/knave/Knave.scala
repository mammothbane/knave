package com.avaglir.knave

import com.avaglir.knave.gamemode.{GameMode, Start}
import com.avaglir.knave.util._
import com.avaglir.knave.util.storage.Pickling._
import org.scalajs.dom._
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.html.{Button, Input, Span}
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
      case (sym: scala.Symbol, disp: Display) => document.getElementById(s"knave-${sym.name}").appendChild(disp.container)
    }

    window.addEventListener("keydown", handleInput _)
    window.addEventListener("keypress", handleInput _)
    displays.values.foreach { _.clear() }
    //    currentMode.render()

    // hacky and dumb
    mousedown = true
    redraw(null)
    mousedown = false

    inputs.values.foreach { input =>
      input.addEventListener("mousedown", (m: MouseEvent) => mousedown = true)
      input.addEventListener("mouseup", (m: MouseEvent) => mousedown = false)
    }

    window.addEventListener("mousemove", redraw _)

    regenerate.addEventListener("click", (m: MouseEvent) => {
      large = simplex.SimplexNoise(random.int(Int.MinValue, Int.MaxValue))
      small = simplex.SimplexNoise(random.int(Int.MinValue, Int.MaxValue))

      // hacky and dumb
      mousedown = true
      redraw(null)
      mousedown = false
    })
  }

  var large = simplex.SimplexNoise(random.int(Int.MinValue, Int.MaxValue))
  var small = simplex.SimplexNoise(random.int(Int.MinValue, Int.MaxValue))

  var mousedown = false
  val mn = displays('main)

  val elems = List(
    "scale-small",
    "scale-large",
    "small-weight",
    "large-weight",
    "large-falloff",
    "small-falloff",
    "threshold"
  )

  val inputs = elems.map { elem => (elem, document.getElementById(elem).asInstanceOf[Input]) }.toMap

  def values(key: String): Float = inputs(key).value.toFloat
  def update() = elems.foreach { elem => {
    val span = document.getElementById(elem + "-text").asInstanceOf[Span]
    span.textContent = values(elem).toString
  }}

  val cutoff = document.getElementById("cutoff").asInstanceOf[Input]
  val regenerate = document.getElementById("regenerate").asInstanceOf[Button]

  val mag = Vector2(mn.width, mn.height).half.magnitude

  def redraw(evt: MouseEvent): Unit = {
    if (!mousedown) return
    println("redrawing")

    update()

    mn.clear()

    val scaleLarge = values("scale-large")
    val scaleSmall = values("scale-small")
    val smallWeight = values("small-weight")
    val largeWeight = values("large-weight")

    val threshold = values("threshold")

    val largeFalloffRadius = mag * values("large-falloff")
    val smallFalloffRadius = mag * values("small-falloff")

    (0 until mn.width).foreach { x =>
      (0 until mn.height).foreach { y =>
        val vec = Vector2(x, y)

        val fromCtr = vec - mn.center

        val smallScale = if (fromCtr.magnitude > smallFalloffRadius) 1 - (fromCtr.magnitude - smallFalloffRadius) / (mag - smallFalloffRadius) else 1
        val largeScale = if (fromCtr.magnitude > largeFalloffRadius) 1 - (fromCtr.magnitude - largeFalloffRadius) / (mag - largeFalloffRadius) else 1

        val v = (large.eval(fromCtr.x.toFloat / scaleLarge, fromCtr.y.toFloat / scaleLarge) * largeWeight.toFloat * largeScale * largeScale +
          small.eval(fromCtr.x.toFloat / scaleSmall, fromCtr.y.toFloat / scaleSmall) * smallWeight.toFloat * smallScale * smallScale)/(largeWeight + smallWeight)

        if (cutoff.checked) {
          displays('main).draw(vec, 'a', HSL(0f, 0f, v.toFloat))
        } else {
          if (v > threshold) displays('main).draw(vec, 'a', Color.WHITE)
        }
      }
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
