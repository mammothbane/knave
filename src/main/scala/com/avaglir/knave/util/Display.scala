package com.avaglir.knave.util

import org.scalajs.dom.Event

import scala.scalajs.js

/**
  * Wrapper for the js type to make things easier to use.
  * @param width The width of the display.
  * @param height The height of the display.
  * @param spacing Spacing factor between tiles.
  */
class Display(width: Int, height: Int, spacing: Float = 1) {
  private val display = new rot.Display(js.Dynamic.literal(width = width, height = height, spacing = spacing))

  def clear() = display.clear()
  def container = display.container()
  def eventPosition(evt: Event) = {
    val pos = display.eventToPosition(evt)
    Vector2(pos._1, pos._2)
  }

  def draw(v: Vector2, ch: Char, fg: Color = Color.WHITE, bg: Color = Color.BLACK) = {
    display.draw(v.x, v.y, s"$ch", fg.hex, bg.hex)
  }

  def drawText(v: Vector2, text: String, maxWidth: Option[Int] = None) = {
    maxWidth match {
      case None => display.drawText(v.x, v.y, text)
      case Some(wid) => display.drawText(v.x, v.y, text, wid)
    }
  }
}
