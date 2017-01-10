package com.avaglir.knave.gamemode

import com.avaglir.knave.map._
import org.scalajs.dom.KeyboardEvent

case class MapMode(map: Map) extends GameMode {
  override def exit(): Unit = ???
  override def frame(evt: KeyboardEvent): Option[GameMode] = ???
  override def render(): Unit = ???
}
