package com.avaglir.knave.map

import com.avaglir.knave.util._

import scala.collection.mutable

case class Nation(
    land: Set[Landmass],
    name: String,
    nClass: NationClass,
    townDensity: UnitClampedFloat,
  ) {
  def print = s"$nClass of $name"
}

sealed abstract class NationClass(val size: Int)

object NationClass {
  case object Barony  extends NationClass(1)
  case object County  extends NationClass(2)
  case object Duchy   extends NationClass(3)
  case object Kingdom extends NationClass(4)
  case object Empire  extends NationClass(5)

  def apply(size: Int) =
    size match {
      case 1 => Barony
      case 2 => County
      case 3 => Duchy
      case 4 => Kingdom
      case 5 => Empire
    }

}

object Nation extends Random {
  private lazy val stateCount = random.int(Landmass.all.size / 3, 2 * Landmass.all.size / 3)

  private val standard = NationClass.County

  implicit def double2Int(d: Double): Int = d.toInt

  /**
   * Find the nation in which the given vector is located. Nation scale assumed.
   *
   * @param vec The location to check for a nation.
   * @return A nation if it exists at the given location. Else None.
   */
  def which(vec: Vector2[Int]) =
    all.find {
      _.land.exists {
        _.tiles.contains(vec)
      }
    }

  lazy val all: Set[Nation] = {
    val totalLand = Landmass.all.map {
      _.area
    }.sum

    val stateClasses = (0 until stateCount).map { _ =>
      poisson(standard.size - 1).clamp(0, 4) + 1
    }.sortBy {
      -_
    }
    val stateSizes = (0 until stateCount).map(_ => poisson(10) + 1).sortBy {
      -_
    }
    val totalStateSize = stateSizes.sum
    val stateAreas = stateSizes.map {
      totalLand.toFloat * _.toFloat / totalStateSize
    }

    val sortedLMs = Landmass.all.toList.sortBy {
      -_.area
    }

    val lmAssigns = mutable.ListBuffer.fill(stateCount) {
      mutable.Set.empty[Landmass]
    }

    lmAssigns.foreach { set =>
      set += sortedLMs.find { elem =>
        !lmAssigns.exists {
          _.contains(elem)
        }
      }.get
    }

    val freeLMs = sortedLMs.filter { elem =>
      !lmAssigns.exists {
        _.contains(elem)
      }
    }
    val remainingArea = freeLMs.map {
      _.area
    }.sum

    val remainingProp = stateSizes.map {
      remainingArea.toFloat * _.toFloat / totalStateSize
    }

    // low scores are desirable
    def score(
        lm: Landmass,
        candidate: mutable.Set[Landmass],
        remainingArea: Float,
      ): Float = {
      val sz = lm.area + candidate.map {
        _.area
      }.sum - remainingArea
      val szComponent       = if (sz <= 0) 0 else sz
      val distanceComponent = candidate.map(elem => (elem.center - lm.center).magnitude).sum

      (distanceComponent * distanceComponent * distanceComponent * distanceComponent + szComponent / 20).toFloat
    }

    freeLMs.foreach { free =>
      lmAssigns.zipWithIndex.minBy {
        case (lms, idx) => score(free, lms, remainingProp(idx))
      }._1 += free
    }

    //    lmAssigns.zipWithIndex.foreach {
    // case (lms, idx) => println(s"Nation class ${NationClass(stateClasses(idx))}, ${lms.size}
    // island(s), distance sum: ${lms.map { lm => (lms - lm).map { elem => (elem.center -
    // lm.center).magnitude }.sum}.sum }, area: ${lms.map { _.area }.sum}")
    //    }

    val unused = mutable.ListBuffer(names.toList: _*)
    lmAssigns.zipWithIndex.map {
      case (lms, idx) =>
        val name = if (unused.nonEmpty) {
          unused.remove(random.int(0, unused.length - 1))
        }
        else NameGen.get

        Nation(lms.toSet, name, NationClass(stateClasses(idx)), random.uniform().toFloat)
    }.toSet
  }

  private val names = Set(
    "Arstotzka",
    "Atlantis",
    "Gondor",
    "Beleriand",
    "Londor",
    "Londo",
    "Vanu",
    "Chadbourne",
    "Ancelstierre",
    "Belisaere",
    "Vandreka",
    "Tirania",
    "Sercia",
    "Oriosa",
    "Nivia",
    "Lukano",
    "Bregna",
    "Bensalem",
    "Panau",
    "Nim",
    "R'lyeh",
    "Nollop",
    "Tsalal",
    "Illyria",
    "Orsinia",
    "Vespugia",
    "Republia",
    "Obristan",
    "Kolechia",
    "Impor",
    "Gilead",
    "Farfelu",
    "Florin",
    "Orleans",
    "Dauphin",
    "Syldavia",
    "Sciriel",
    "Vascovy",
    "Queelag",
    "Carthus",
    "Ambrosia",
    "Avalon",
    "Avernus",
    "Xethoila",
    "Resceasal",
    "Atrulor",
    "Esharia",
    "Cadrar",
  )

  object NameGen {

    // borrowed from http://fantasynamegenerators.com/scripts/landNames.js
    private val nm1 = List(
      "b",
      "c",
      "d",
      "f",
      "g",
      "h",
      "i",
      "j",
      "k",
      "l",
      "m",
      "n",
      "p",
      "q",
      "r",
      "s",
      "t",
      "v",
      "w",
      "x",
      "y",
      "z",
      "",
      "",
      "",
      "",
      "",
    )

    private val nm2 = List("a", "e", "o", "u")

    private val nm3 = List(
      "br",
      "cr",
      "dr",
      "fr",
      "gr",
      "pr",
      "str",
      "tr",
      "bl",
      "cl",
      "fl",
      "gl",
      "pl",
      "sl",
      "sc",
      "sk",
      "sm",
      "sn",
      "sp",
      "st",
      "sw",
      "ch",
      "sh",
      "th",
      "wh",
    )

    private val nm4 = List(
      "ae",
      "ai",
      "ao",
      "au",
      "a",
      "ay",
      "ea",
      "ei",
      "eo",
      "eu",
      "e",
      "ey",
      "ua",
      "ue",
      "ui",
      "uo",
      "u",
      "uy",
      "ia",
      "ie",
      "iu",
      "io",
      "iy",
      "oa",
      "oe",
      "ou",
      "oi",
      "o",
      "oy",
    )

    private val nm5 = List(
      "stan",
      "dor",
      "vania",
      "nia",
      "lor",
      "cor",
      "dal",
      "bar",
      "sal",
      "ra",
      "la",
      "lia",
      "jan",
      "rus",
      "ze",
      "tan",
      "wana",
      "sil",
      "so",
      "na",
      "le",
      "bia",
      "ca",
      "ji",
      "ce",
      "ton",
      "ssau",
      "sau",
      "sia",
      "ca",
      "ya",
      "ye",
      "yae",
      "tho",
      "stein",
      "ria",
      "nia",
      "burg",
      "nia",
      "gro",
      "que",
      "gua",
      "qua",
      "rhiel",
      "cia",
      "les",
      "dan",
      "nga",
      "land",
    )

    private val nm6 = List(
      "ia",
      "a",
      "en",
      "ar",
      "istan",
      "aria",
      "ington",
      "ua",
      "ijan",
      "ain",
      "ium",
      "us",
      "esh",
      "os",
      "ana",
      "il",
      "ad",
      "or",
      "ea",
      "eau",
      "ax",
      "on",
      "ana",
      "ary",
      "ya",
      "ye",
      "yae",
      "ait",
      "ein",
      "urg",
      "al",
      "ines",
      "ela",
    )

    private val nameParts = List(nm1, nm2, nm3, nm4, nm5, nm6)

    def get: String = {
      val rands = nameParts.map(part => part(random.int(0, part.length - 1)))

      (random.int(0, 4) match {
        case 0 => 0 until 5
        case 1 => List(0, 1, 2, 5)
        case 2 => 2 until 5
        case 3 => List(1, 2, 5)
        case _ => List(2, 3, 0, 2, 5)
      }).map(rands.apply).mkString
    }

  }

}
