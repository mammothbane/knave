package com.avaglir.knave.entities

import com.avaglir.knave.util.{Display, _}

trait GameObject {
  def char: Char = ' '
  def fg: Color = Color.WHITE
  def bg: Color = Color.BLACK

  def displayPriority: Int = 0
  def draw(display: Display, loc: Vector2, fgOverride: Option[Color] = None, bgOverride: Option[Color] = None) = {
    val f = fgOverride match {
      case Some(color) => color
      case None => fg
    }

    val b = bgOverride match {
      case Some(color) => color
      case None => bg
    }

    display.draw(loc, char, f, b)
  }
}
