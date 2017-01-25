package com.avaglir.knave.map

import com.avaglir.knave.entities.GameObject
import com.avaglir.knave.util.{Color, RenderTile}

case class Tile(
                 name: String,
                 repr: RenderTile,
                 pathable: Boolean = false,
                 opaque: Boolean = false,
                 debug: Boolean = false
               ) extends GameObject {

  def transparent = !opaque

  override def toString: String = s"Tile('${repr.char}', pathable = $pathable, opaque = $opaque)"
}

object Tile {
  val WALL = Tile("wall", RenderTile('#'), opaque = true)
  val FLOOR = Tile("floor", RenderTile('.'), pathable = true)
  val WATER = Tile("Water", RenderTile('~', fg = Color.BLUE))
}
