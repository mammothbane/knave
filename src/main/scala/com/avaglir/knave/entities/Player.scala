package com.avaglir.knave.entities
import com.avaglir.knave.util._

object Player extends Entity {
  val repr = RenderTile('@', HSL(Color.YELLOW.hue, 0.67f, 0.7f))
  override var loc: Vector2[Int] = Vector2.ZERO[Int]
}
