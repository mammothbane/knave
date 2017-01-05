package com.avaglir.knave.map

import com.avaglir.knave.entities.GameObject

case class Tile(
                 name: String,
                 override val char: Char,
                 pathable: Boolean = false,
                 opaque: Boolean = false,
                 diggable: Boolean = false
               ) extends GameObject {

  def transparent = !opaque

  override def toString: String = s"Tile('$char', pathable = $pathable, opaque = $opaque, diggable = $diggable)"
}

object Tile {
  val WALL = Tile("wall", '#', opaque = true)
  val FLOOR = Tile("floor", '.', pathable = true)
}
