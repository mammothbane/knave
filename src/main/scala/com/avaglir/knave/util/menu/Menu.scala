package com.avaglir.knave.util.menu

import com.avaglir.knave.input.{Action, translate}
import com.avaglir.knave.util._
import org.scalajs.dom.KeyboardEvent

import scala.collection.mutable

case class Menu(items: mutable.ListBuffer[(String, Symbol)], pointer: String, private var index: Int = 0) {
  def input(evt: KeyboardEvent): Option[Symbol] = {
    translate(evt) match {
      case Some(a) => a match {
        case Action.DOWN => index = (index - 1) % items.length
        case Action.UP => index = (index + 1) % items.length
        case Action.INTERACT => return Some(items(indexValue)._2)
        case _ =>
      }
      case None =>
    }
    None
  }

  private def indexValue = (index + items.length) % items.length
  private lazy val ptrLength = pointer.trim.decolorize.length

  def draw(d: Display, v: Vector2): Unit = {
    val longestStr = items.map{ case (str, _) => str.decolorize }.maxBy { _.length }
    if (v.x + ptrLength + 1 + longestStr.length > d.width) throw new IllegalArgumentException(s"Cannot draw menu at $v: too wide.")

    items.zipWithIndex.foreach {
      case ((str, _), idx) => d.drawText(v + Vector2.DOWN * idx, str)
    }

    d.drawText(v + (Vector2.DOWN * indexValue) + Vector2.LEFT * (ptrLength + 1), pointer.trim)
  }
}
