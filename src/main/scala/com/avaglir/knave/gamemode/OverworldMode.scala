package com.avaglir.knave.gamemode

import com.avaglir.knave.Knave
import org.scalajs.dom.KeyboardEvent

object OverworldMode extends GameMode {
  val main = Knave.displays('main)
//  Overworld.setCenter(Player.loc)

  override def exit(): Unit = {}

  override def frame(evt: KeyboardEvent): Option[GameMode] = {
    None
  }

  override def render(): Unit = {
//    val tiles = Overworld.render(Player.loc, main.extents)
//    val origin = Player.loc - tiles.extents/2
//
//    ShadowRaycast.calculate(main.center, 10, vec => {
//      tiles(vec).transparent
//    }).foreach { vec =>
//      tiles(vec).draw(main, vec)
//    }
  }
}
