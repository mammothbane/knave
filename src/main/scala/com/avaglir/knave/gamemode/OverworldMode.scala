package com.avaglir.knave.gamemode

import com.avaglir.knave.entities.Player
import com.avaglir.knave.input.Action
import com.avaglir.knave.map.{Chunk, Overworld}
import com.avaglir.knave.util._
import com.avaglir.knave.{Knave, input}
import org.scalajs.dom.KeyboardEvent

object OverworldMode extends GameMode {
  val main = Knave.displays('main)
  Overworld.setCenter(Player.loc)

  override def exit(): Unit = {}

  override def frame(evt: KeyboardEvent): Option[GameMode] = {
    input.translate(evt) match {
      case Some(act) =>
        val nw = act match {
          case Action.UP => Vector2.UP[Int]
          case Action.DOWN => Vector2.DOWN[Int]
          case Action.LEFT => Vector2.LEFT[Int]
          case Action.RIGHT => Vector2.RIGHT[Int]
          case _ => Vector2.ZERO[Int]
        }
        Player.loc = (Player.loc + nw).clamp(Vector2.ZERO[Int], Vector2.UNIT[Int] * Chunk.TILE_DIMENS)
      case None =>
    }

    Overworld.setCenter(Player.loc)

    None
  }

  override def render(): Unit = {
    val tiles = Overworld.render(Player.loc, main.extents)
    val origin = Player.loc - tiles.extents/2

    println(s"Player location: ${Player.loc}")

    ShadowRaycast.calculate(main.center, 10, vec => {
      tiles(vec).transparent
    }).foreach { vec =>
      tiles(vec).draw(main, vec)
    }

    Player.draw(main, main.center)
  }
}
