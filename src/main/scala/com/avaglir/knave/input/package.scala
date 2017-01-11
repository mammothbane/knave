package com.avaglir.knave

import com.avaglir.knave.input.Action._
import org.scalajs.dom.KeyboardEvent
import org.scalajs.dom.ext.KeyCode._

/**
  * Created by mammothbane on 1/11/2017.
  */
package object input {
  final val defaultBinds = Map(
    E -> INTERACT,
    Enter -> INTERACT,

    W -> UP,
    Up -> UP,

    A -> LEFT,
    Left -> LEFT,

    S -> DOWN,
    Down -> DOWN,

    D -> RIGHT,
    Right -> RIGHT,

    Space -> SKIP
  )

  def translate(evt: KeyboardEvent): Option[Action] = defaultBinds.get(evt.keyCode)
}
