package com.avaglir.knave.gamemode
import com.avaglir.knave.Knave
import com.avaglir.knave.util._
import org.scalajs.dom.KeyboardEvent

class Start extends GameMode {
  private val KNAVE =
    """
      | _        _        _______           _______
      || \    /\( (    /|(  ___  )|\     /|(  ____ \
      ||  \  / /|  \  ( || (   ) || )   ( || (    \/
      ||  (_/ / |   \ | || (___) || |   | || (__
      ||   _ (  | (\ \) ||  ___  |( (   ) )|  __)
      ||  ( \ \ | | \   || (   ) | \ \_/ / | (
      ||  /  \ \| )  \  || )   ( |  \   /  | (____/\
      ||_/    \/|/    )_)|/     \|   \_/   (_______/
    """.trim.stripMargin

  val mainColor = Color("#15d34e")
  val kCenter = Vector2(KNAVE.split('\n').maxBy{ _.length }.length, KNAVE.split('\n').length).half
  val offset = (Knave.displays('main).center - kCenter) + Vector2.UP * 2

  var load = false

  override def frame(evt: KeyboardEvent): Option[GameMode] = {
    if (evt.`type` != "keydown") return None

    evt.key.toLowerCase match {
//      case "e" => Some(MapMode())
      case "w" | "s" =>
        load = !load
        None
      case _ => None
    }
  }

  override def render(): Unit = {
    val main = Knave.displays('main)
    main.drawText(offset, KNAVE, fg = mainColor, trim = false)

    val menuOffset = main.center + Vector2(-2, kCenter.y + 1)
    main.drawText(menuOffset, "New")
    main.drawText(menuOffset + Vector2.DOWN, "Load")
    main.drawText(menuOffset + Vector2(-3, if (load) 1 else 0), "=>".colorize(Color("#1b58d3")))
  }

  override def exit(): Unit = {}

  override def persist(): Map[Symbol, String] = Map()
}
