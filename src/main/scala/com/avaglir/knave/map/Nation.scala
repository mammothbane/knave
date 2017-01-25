package com.avaglir.knave.map

import com.avaglir.knave.util._

import scala.collection.mutable

case class Nation(land: Set[Landmass], name: String, nClass: NationClass, townDensity: UnitClampedFloat) {}

sealed abstract class NationClass(val size: Int)

object NationClass {
  case object Barony extends NationClass(1)
  case object County extends NationClass(2)
  case object Duchy extends NationClass(3)
  case object Kingdom extends NationClass(4)
  case object Empire extends NationClass(5)

  def apply(size: Int) = size match {
    case 1 => Barony
    case 2 => County
    case 3 => Duchy
    case 4 => Kingdom
    case 5 => Empire
  }
}

object Nation extends Persist with Random {
  private lazy val stateCount = random.int(Landmass.all.size/3, 2*Landmass.all.size/3)

  private val standard = NationClass.County

  implicit def double2Int(d: Double): Int = d.toInt

  /**
    * Find the nation in which the given vector is located. Nation scale assumed.
    * @param vec The location to check for a nation.
    * @return A nation if it exists at the given location. Else None.
    */
  def which(vec: Vector2[Int]) = Nation.all.find { _.land.exists { _.tiles.contains(vec) }}

  lazy val all: Set[Nation] = {
    val totalLand = Landmass.all.map { _.area }.sum

    val stateClasses = (0 until stateCount).map { _ => poisson(standard.size - 1).clamp(0, 4) + 1 }.sortBy { -_ }
    val stateSizes = (0 until stateCount).map { _ => poisson(10) + 1 }.sortBy { -_ }
    val totalStateSize = stateSizes.sum
    val stateAreas = stateSizes.map { totalLand.toFloat * _.toFloat / totalStateSize }

    val sortedLMs = Landmass.all.toList.sortBy { -_.area }

    val lmAssigns = mutable.ListBuffer.fill(stateCount){mutable.Set.empty[Landmass]}

    lmAssigns.foreach { set =>
      set += sortedLMs.find { elem => !lmAssigns.exists { _.contains(elem) }}.get
    }

    val freeLMs = sortedLMs.filter { elem => !lmAssigns.exists{ _.contains(elem) } }
    val remainingArea = freeLMs.map { _.area }.sum

    val remainingProp = stateSizes.map { remainingArea.toFloat * _.toFloat / totalStateSize }

    // low scores are desirable
    def score(lm: Landmass, candidate: mutable.Set[Landmass], remainingArea: Float): Float = {
      val sz = lm.area + candidate.map { _.area }.sum - remainingArea
      val szComponent = if (sz <= 0) 0 else sz
      val distanceComponent = candidate.map { elem => (elem.center - lm.center).magnitude }.sum

      (distanceComponent * distanceComponent * distanceComponent * distanceComponent + szComponent/20).toFloat
    }

    freeLMs.foreach { free =>
      lmAssigns.zipWithIndex.minBy { case (lms, idx) => score(free, lms, remainingProp(idx)) }._1 += free
    }

//    lmAssigns.zipWithIndex.foreach {
//      case (lms, idx) => println(s"Nation class ${NationClass(stateClasses(idx))}, ${lms.size} island(s), distance sum: ${lms.map { lm => (lms - lm).map { elem => (elem.center - lm.center).magnitude }.sum}.sum }, area: ${lms.map { _.area }.sum}")
//    }

    val unused = mutable.ListBuffer(nameList.toList: _*)
    lmAssigns.zipWithIndex.map {
      case (lms, idx) =>
        val nameIndex = random.int(0, unused.length)
        val name = unused(nameIndex)
        unused.remove(nameIndex)

        Nation(lms.toSet, name, NationClass(stateClasses(idx)), random.uniform().toFloat)
    }.toSet
  }

  override def persist(): Map[Symbol, String] = Map(
    'random -> seed.toString
  )

  override def restore(v: Map[Symbol, String]): Unit = {
    setSeed(v('random).toDouble)
  }

  override def key: Symbol = 'nation

  private val nameList = Set(
    "Arstotzka", "Atlantis", "Gondor", "Beleriand", "Londor", "Londo", "Vanu", "Chadbourne", "Ancelstierre", "Belisaere",
    "Vandreka", "Tirania", "Sercia", "Oriosa", "Nivia", "Lukano", "Bregna", "Bensalem", "Panau", "Nim", "R'lyeh",
    "Nollop", "Tsalal", "Illyria", "Orsinia", "Vespugia", "Republia", "Obristan", "Kolechia", "Impor", "Gilead", "Farfelu",
    "Florin", "Orleans", "Dauphin", "Syldavia", "Sciriel", "Vascovy"
  )
}
