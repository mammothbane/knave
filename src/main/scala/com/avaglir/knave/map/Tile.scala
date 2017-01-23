package com.avaglir.knave.map

import com.avaglir.knave.entities.GameObject
import com.avaglir.knave.util.Color

case class Tile(
                 name: String,
                 override val char: Char,
                 override val fg: Color = Color.WHITE,
                 override val bg: Color = Color.BLACK,
                 pathable: Boolean = false,
                 opaque: Boolean = false,
                 debug: Boolean = false
               ) extends GameObject {

  def transparent = !opaque

  override def toString: String = s"Tile('$char', pathable = $pathable, opaque = $opaque)"
}

object Tile {
  val WALL = Tile("wall", '#', opaque = true)
  val FLOOR = Tile("floor", '.', pathable = true)
  val WATER = Tile("Water", '~', fg = Color.BLUE)
}
