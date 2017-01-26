package com.avaglir.knave.gamemode

import com.avaglir.knave.entities.Player
import com.avaglir.knave.input.Action
import com.avaglir.knave.map.{Chunk, Overworld}
import com.avaglir.knave.util._
import com.avaglir.knave.{Knave, input}
import org.scalajs.dom.KeyboardEvent

object OverworldMode extends GameMode {
  val main = Knave.displays('main)
  Overworld.setCenter(Player.loc)

  override def exit(): Unit = {}

  override def frame(evt: KeyboardEvent): Option[GameMode] = {
    if (evt.`type` != "keydown") return None

    input.translate(evt) match {
      case Some(act) =>
        val tgt = Player.loc + ((act match {
          case Action.UP => Vector2.UP[Int]
          case Action.DOWN => Vector2.DOWN[Int]
          case Action.LEFT => Vector2.LEFT[Int]
          case Action.RIGHT => Vector2.RIGHT[Int]
          case _ => Vector2.ZERO[Int]
        }) * (if (evt.shiftKey) Chunk.DIMENS else 1))

        println(s"target: $tgt")

        Player.loc = tgt.clamp(Vector2.ZERO[Int], Vector2.UNIT[Int] * Chunk.TILE_DIMENS)
      case None =>
    }

    Overworld.setCenter(Player.loc)

    None
  }

  private def screenOrigin = (Player.loc - main.extents.half).clamp(Vector2.ZERO[Int], Vector2.UNIT[Int] * Chunk.TILE_DIMENS)

  val waterBlue = HSL(Color.BLUE.hue, 0.3f, 0.07f)

  override def render(): Unit = {
    val tiles = Overworld.render(Player.loc, main.extents)
    val seen = Overworld.seen(Player.loc, main.extents)

    Knave.displays('status).drawText(Vector2.UNIT[Int], s"Tile: ${Player.loc}")
    Knave.displays('status).drawText(Vector2(1, 2), s"Chunk: ${Player.loc/Chunk.DIMENS}")

//    println(s"Player location: ${Player.loc}")

    val ext = main.extents
    (0 until ext.x).cartesianProduct(0 until ext.y).foreach {
      case (x, y) => main.draw(Vector2(x,y), ' ', bg = waterBlue)
    }

    seen.foreach { vec =>
      val rt = tiles(vec).repr
      val repr = RenderTile(rt.char, rt.fg.darker, rt.bg.darker, rt.debug)

      repr.draw(main, vec)
    }

    ShadowRaycast.calculate(main.center, 6, vec => {
      tiles(vec).transparent
    }).foreach { vec =>
      val tileLoc = screenOrigin + vec
      if (tileLoc.componentsClamped(Vector2.UNIT[Int] * Chunk.TILE_DIMENS)) Overworld.markSeen(tileLoc)
      tiles(vec).draw(main, vec)
    }

    Player.draw(main, main.center)
  }
}
