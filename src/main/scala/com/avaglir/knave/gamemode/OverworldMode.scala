package com.avaglir.knave.gamemode

import com.avaglir.knave.entities.Player
import com.avaglir.knave.input.Action
import com.avaglir.knave.map.Overworld
import com.avaglir.knave.util._
import com.avaglir.knave.{Knave, input}
import org.scalajs.dom.KeyboardEvent

object OverworldMode extends GameMode {
  val main = Knave.displays('main)
  Overworld.setCenter(Player.loc)

  override def exit(): Unit = {}

  override def frame(evt: KeyboardEvent): Option[GameMode] = {
    input.translate(evt) match {
      case Some(act) => act match {
        case Action.UP => Player.loc += Vector2.UP[Int]
        case Action.DOWN => Player.loc = Vector2.UP[Int]
        case Action.LEFT => Player.loc = Vector2.UP[Int]
        case Action.RIGHT => Player.loc = Vector2.UP[Int]
        case _ =>
      }
      case None =>
    }

    Overworld.setCenter(Player.loc)

    None
  }

  override def render(): Unit = {
    val tiles = Overworld.render(Player.loc, main.extents)
    val origin = Player.loc - tiles.extents/2

    ShadowRaycast.calculate(main.center, 10, vec => {
      tiles(vec).transparent
    }).foreach { vec =>
      tiles(vec).draw(main, vec)
    }
  }
}
