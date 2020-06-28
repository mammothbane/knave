package com.avaglir.knave.gamemode

import com.avaglir.knave.Knave
import com.avaglir.knave.util._
import com.avaglir.knave.util.menu.{Entry, Menu}
import org.scalajs.dom.KeyboardEvent

import scala.collection.mutable

object Start extends GameMode {

  // note: i had to use fucking macrons for this because for some reason ROT won't draw underscores
  // in certain
  // locations. NO IDEA WHY. breaks in chrome, works fine in firefox.
  private val KNAVE =
    """
      ||¯\    /\(¯(    /|(¯¯¯¯¯¯¯)|\     /|(¯¯¯¯¯¯¯\
      ||  \  / /|  \  ( || (¯¯¯) || )   ( || (¯¯¯¯\/
      ||  ( / / |   \ | || (   ) || |   | || (
      ||   ¯ (  | (\ \) ||  ¯¯¯  |( (   ) )|  ¯¯)
      ||  (¯\ \ | | \   || (¯¯¯) | \ \ / / | (¯¯
      ||  /  \ \| )  \  || )   ( |  \ ¯ /  | (    /\
      || /    \/|/    ) )|/     \|   \ /   (  ¯¯¯¯ /
      | ¯              ¯              ¯     ¯¯¯¯¯¯¯
    """.trim.stripMargin

  val mainColor = c"#15d34e"

  val kCenter = Vector2(
    KNAVE.split('\n').maxBy {
      _.length
    }.length,
    KNAVE.split('\n').length,
  ).half

  val offset = (Knave.displays('main).center - kCenter) + Vector2.UP[Int] * 1

  val menu = Menu(
    mutable.ListBuffer(Entry("New", 'new), Entry("Load", 'load, enabled = false)),
    "=>".colorize(c"#1b58d3"),
  )

  override def enter(): Unit = {}

  override def frame(evt: KeyboardEvent): Option[GameMode] = {
    if (evt.`type` != "keydown") return None

    menu.input(evt) match {
      case Some('new)  => Some(OverworldMode)
      case Some('load) => None
      case _           => None
    }
  }

  override def render(): Unit = {
    val main = Knave.displays('main)
    main.drawText(offset, KNAVE, fg = mainColor, trim = false)

    val menuOffset = main.center + Vector2(-2, kCenter.y + 1)
    menu.draw(main, menuOffset)
  }

  override def exit(): Unit = {}
}
