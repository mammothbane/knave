package com.avaglir.knave.gamemode

import com.avaglir.knave.entities.Player
import com.avaglir.knave.input.Action
import com.avaglir.knave.items.{Armor, Equippable, GearSlot, Weapon}
import com.avaglir.knave.map._
import com.avaglir.knave.properties.{Equipped, Fighter}
import com.avaglir.knave.util._
import com.avaglir.knave.{Knave, input}
import org.scalajs.dom.KeyboardEvent

object OverworldMode extends GameMode with Random {
    val main = Knave.displays('main)
    val status = Knave.displays('status)
    var obs: Option[Vector2[Int]] = None

    override def enter(): Unit = {
        Overworld.setCenter(Player.loc)
        obs = Some(Player.loc + Vector2(3, 3))
    }

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

        val inWater = tiles(main.extents.half) == Tile.WATER

        val currentNation = Nation.which(Player.loc / Chunk.DIMENS / 8)
        status.drawText(Vector2(1, Knave.displays('status).height - 3), currentNation match {
            case Some(nation) => nation.print
            case None if !inWater =>
                Nation.all.minBy {
                    _.land.map { lm => (lm.center * 8 * Chunk.DIMENS - Player.loc).magnitude }.min
                }.print
            case None => "Unknown waters"
        })

        status.drawText(Vector2(1, Knave.displays('status).height - 2),
            if (inWater) "At sea" else {
                Landmass.which(Player.loc / Chunk.DIMENS / 8) match {
                    case Some(lm) => lm.print
                    case None => Landmass.all.minBy { lm => (lm.center * 8 * Chunk.DIMENS - Player.loc).magnitude }.print
                }
            }, Color.WHITE.darker)

        status.drawText(Vector2(1, 1), "STATUS")

        val elems = Player.message(Equipped.equipped)("armored").asInstanceOf[Map[GearSlot, Equippable]]
        val stats = Player.message(Fighter.stats)("fighter").asInstanceOf[Fighter.Stats]
        val armor = elems.unzip._2.map {
            case x: Armor => x.baseArmor
            case _ => 0
        }.sum

        val damage = elems.get(GearSlot.Weapon).map {
            _.asInstanceOf[Weapon].baseDamage
        }.getOrElse(0)

        status.drawText(Vector2(2, 3), s"Health:   ${stats.health._1.toString.colorize(Color.RED)}/${stats.health._2}")
        status.drawText(Vector2(2, 4), s"Damage:   ${damage.toString.colorize(c"#e29d1d")}")
        status.drawText(Vector2(2, 5), s"Armor:    ${armor.toString.colorize(Color.GREEN.darker)}")
        status.drawText(Vector2(2, 6), s"Accuracy: ${(stats.accuracy * 100).toInt.toString.colorize(c"#3675db")}%")

        status.drawText(Vector2(1, 9), "EQUIPMENT")

        elems.take(5).zipWithIndex.foreach { case ((slot, eq), index) =>
            status.drawText(Vector2(2, 11 + index), s"${slot.name.titleCase.colorize(c"#36db80")}: ${eq.name.titleCase}")
        }

        Overworld.debug.tile.textContent = s"Tile: ${Player.loc}"
        Overworld.debug.chunk.textContent = s"Chunk: ${Player.loc / Chunk.DIMENS}"

        //    println(s"Player location: ${Player.loc}")

        val ext = main.extents
        (0 until ext.x).cartesianProduct(0 until ext.y).foreach {
            case (x, y) => main.draw(Vector2(x, y), ' ', bg = waterBlue)
        }

        seen.foreach { vec =>
            val rt = tiles(vec).repr
            val repr = RenderTile(rt.char, rt.fg.darker, rt.bg.darker, rt.debug)

            repr.draw(main, vec)
        }

        ShadowRaycast.calculate(main.center, 15, vec => {
            if (vec.componentsClamped(main.extents)) {
                if (vec == obs.get - screenOrigin) false
                else tiles(vec).transparent
            } else false
        }).foreach { vec =>
            val tileLoc = screenOrigin + vec
            if (tileLoc.componentsClamped(Vector2.UNIT[Int] * Chunk.TILE_DIMENS)) Overworld.markSeen(tileLoc)
            tiles(vec).draw(main, vec)
        }

        Player.draw(main, main.center)
    }
}
