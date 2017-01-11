package com.avaglir.knave.gamemode

import com.avaglir.knave.map._
import org.scalajs.dom.KeyboardEvent

case class MapMode(map: GameMap) extends GameMode {
  override def exit(): Unit = {

  }

  override def frame(evt: KeyboardEvent): Option[GameMode] = {
    None
  }

  override def render(): Unit = {

  }

  override def persist(): Map[Symbol, String] = {
    Map()
  }
}
