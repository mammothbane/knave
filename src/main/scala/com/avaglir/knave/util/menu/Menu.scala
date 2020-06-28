package com.avaglir.knave.util.menu

import com.avaglir.knave.input.{translate, Action}
import com.avaglir.knave.util._
import org.scalajs.dom.KeyboardEvent

import scala.collection.mutable

case class Menu(
    entries: mutable.ListBuffer[Entry],
    pointer: String,
    private var index: Int = 0,
  ) {

  def input(evt: KeyboardEvent): Option[Symbol] = {
    translate(evt) match {
      case Some(a) => a match {
          case Action.DOWN =>
            if (!entries.exists(_.enabled)) return None

            var done = false
            while (!done) {
              index = (index + 1) % entries.length
              if (entries(indexValue).enabled) done = true
            }
          case Action.UP =>
            if (!entries.exists(_.enabled)) return None
            var done = false
            while (!done) {
              index = (index - 1) % entries.length
              if (entries(indexValue).enabled) done = true
            }
          case Action.INTERACT => return Some(entries(indexValue).result)
          case _               =>
        }
      case None =>
    }
    None
  }

  def disable(key: Symbol) =
    entries.filter {
      _.result == key
    }.foreach {
      _.enabled = false
    }

  def enable(key: Symbol) =
    entries.filter {
      _.result == key
    }.foreach {
      _.enabled = true
    }

  private def indexValue = (index + entries.length) % entries.length

  private lazy val ptrLength = pointer.trim.decolorize.length

  def draw(d: Display, v: IntVec): Unit = {
    val longestStr = entries.map {
      _.name.decolorize
    }.maxBy {
      _.length
    }
    if (v.x + ptrLength + 1 + longestStr.length > d.width)
      throw new IllegalArgumentException(s"Cannot draw menu at $v: too wide.")

    entries.zipWithIndex.foreach {
      case (entry, idx) =>
        val draw = if (entry.enabled) entry.name else entry.name.colorize(entry.disabledColor)
        d.drawText(v + Vector2.DOWN[Int] * idx, draw)
    }

    d.drawText(
      v + (Vector2.DOWN[Int] * indexValue) + Vector2.LEFT[Int] * (ptrLength + 1),
      pointer.trim,
    )
  }

}
