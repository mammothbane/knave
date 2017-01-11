package com.avaglir.knave.gamemode

import com.avaglir.knave.util._
import org.scalajs.dom.KeyboardEvent

trait GameMode extends Guid with Persist {
  def exit(): Unit
  def frame(evt: KeyboardEvent): Option[GameMode]
  def render(): Unit
}
