package com.avaglir.knave.gamemode

import org.scalajs.dom.KeyboardEvent

object OverworldMode extends GameMode {
  override def exit(): Unit = {

  }

  override def frame(evt: KeyboardEvent): Option[GameMode] = {
    None
  }

  override def render(): Unit = {

  }
}
