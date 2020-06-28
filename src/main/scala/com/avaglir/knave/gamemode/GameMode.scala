package com.avaglir.knave.gamemode

import org.scalajs.dom.KeyboardEvent

trait GameMode {
    def enter(): Unit
    def exit(): Unit
    def frame(evt: KeyboardEvent): Option[GameMode]
    def render(): Unit
}
