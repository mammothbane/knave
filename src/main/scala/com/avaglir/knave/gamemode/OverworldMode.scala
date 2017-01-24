package com.avaglir.knave.gamemode

import com.avaglir.knave.Knave
import com.avaglir.knave.map.Overworld
import org.scalajs.dom.KeyboardEvent

object OverworldMode extends GameMode {
  val main = Knave.displays('main)

  override def exit(): Unit = {

  }

  override def frame(evt: KeyboardEvent): Option[GameMode] = {
    None
  }

  override def render(): Unit = {
    Overworld.render()

  }
}
