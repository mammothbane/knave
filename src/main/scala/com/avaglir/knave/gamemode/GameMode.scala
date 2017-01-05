package com.avaglir.knave.gamemode

import org.scalajs.dom.KeyboardEvent

trait GameMode {
  def exit(): Unit
  def frame(evt: KeyboardEvent)
  def render(): Unit
}
