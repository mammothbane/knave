package com.avaglir.knave.util

import org.scalajs.dom.Event

import scala.scalajs.js

/**
 * Wrapper for the js type to make things easier to use.
 *
 * @param width   The width of the display.
 * @param height  The height of the display.
 * @param spacing Spacing factor between tiles.
 */
class Display(val width: Int, val height: Int, val spacing: Float = 1) {

    private val display: rot.Display = new rot.Display(js.Dynamic.literal(width = width, height = height, spacing = spacing))

    def clear() = display.clear()

    def container = display.container()

    def eventPosition(evt: Event) = {
        val pos = display.eventToPosition(evt)
        Vector2(pos._1, pos._2)
    }

    def draw(v: IntVec, ch: Char, fg: Color = Color.WHITE, bg: Color = Color.BLACK) = {
        display.draw(v.x, v.y, new String(Array(ch)), fg.hex, bg.hex)
    }

    def inBounds(v: IntVec) = v.x < width && v.x >= 0 && v.y < height && v.y >= 0

    def drawText(v: IntVec, text: String, fg: Color = Color.WHITE, bg: Color = Color.BLACK, maxWidth: Option[Int] = None, trim: Boolean = true) = {
        val pairs = text.split('\n').map { line =>
            val offset = if (!trim) line.takeWhile {
                _ == ' '
            }.length else 0
            (line.substring(offset).colorize(fg, bg), offset)
        }.zipWithIndex

        pairs.foreach {
            case ((line, offset), index) =>
                maxWidth match {
                    case None => display.drawText(v.x + offset, v.y + index, line)
                    case Some(wid) => display.drawText(v.x + offset, v.y + index, line, wid)
                }
        }
    }

    def center = Vector2(width, height).half

    def extents = Vector2(width, height)
}

object Display {
    private val colorCache = new Lru[Color, String](25, {
        _.hex
    }, None)

}