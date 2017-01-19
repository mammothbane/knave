package com.avaglir.knave.map

import com.avaglir.knave.util._

import scala.collection.mutable

case class Nation(land: Set[Landmass], name: String, nClass: NationClass) {}

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
  private lazy val stateCount = random.int(Landmass.all.size/2, Landmass.all.size)

  private val standard = NationClass.County

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

    freeLMs.foreach { free =>
      lmAssigns.zipWithIndex.find { case (lms, idx) => lms.head.area < remainingProp(idx) } match {
        case Some(set) => set._1 += free
        case None => lmAssigns(random.int(0, lmAssigns.length - 1)) += free
      }
    }

    (0 until stateCount).foreach { idx =>
      println(s"target: ${stateAreas(idx)}, actual: ${lmAssigns(idx).map { _.area }.sum }")
    }

    lmAssigns.zipWithIndex.map {
      case (lms, idx) => Nation(lms.toSet, "", NationClass(stateClasses(idx)))
    }.toSet
  }

  override def persist(): Map[Symbol, String] = Map(
    'random -> seed.toString
  )

  override def restore(v: Map[Symbol, String]): Unit = {
    setSeed(v('random).toDouble)
  }

  override def key: Symbol = 'nation
}
