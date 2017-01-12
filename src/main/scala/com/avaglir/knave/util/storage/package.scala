package com.avaglir.knave.util

import com.avaglir.knave.Knave
import com.avaglir.knave.gamemode.{GameMode, MapMode, Start}
import com.avaglir.knave.map.{GameMap, Simplex, Tile}
import com.avaglir.knave.util.storage.Macros._
import org.scalajs.dom.window
import prickle.{CompositePickler, _}
import simplex.SimplexNoise

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.util.{Failure, Success, Try}

package object storage {
  private val STORAGE_TEST = "__TEST"
  lazy val ok = Try {
    window.localStorage.setItem(STORAGE_TEST, STORAGE_TEST)
    window.localStorage.removeItem(STORAGE_TEST)
  } match {
    case Success(_) => true
    case Failure(e) =>
      println(s"Local storage not supported on this browser: $e")
      false
  }

  object Pickling {
    implicit val symbolPickler = new Pickler[Symbol] {
      override def pickle[P](obj: Symbol, state: PickleState)(implicit config: PConfig[P]): P = config.makeString(obj.name)
    }

    implicit val symbolUnpickler = new Unpickler[Symbol] {
      override def unpickle[P](pickle: P, state: mutable.Map[String, Any])(implicit config: PConfig[P]): Try[Symbol] = config.readString(pickle).map {
        Symbol(_)
      }
    }

    implicit def arrayPickler[T: Pickler] = new Pickler[Array[T]] {
      override def pickle[P](obj: Array[T], state: PickleState)(implicit config: PConfig[P]): P = {
        Pickler.resolvingSharingCollection(obj, obj.map { e => Pickle(e, state) }, state, config)
      }
    }

    implicit def arrayUnpickler[T: Unpickler : ClassTag] = new Unpickler[Array[T]] {
      override def unpickle[P](pickle: P, state: mutable.Map[String, Any])(implicit config: PConfig[P]): Try[Array[T]] = {
        Unpickler.unpickleSeqish[T, collection.immutable.List, P](pickle, state).map {
          _.toArray
        }
      }
    }

    implicit val modePickler = CompositePickler[GameMode].concreteType[Start].concreteType[MapMode]
    implicit val colorPickler = CompositePickler[Color].concreteType[RGB].concreteType[HSL]
  }

  import Pickling._

  private val HAS_SAVE = 'save_present

  val pkl = Pickle.intoString[Array[Boolean]](Array[Boolean](true, false, true))
  println(pkl)
  Unpickle[Array[Boolean]].fromString(pkl).get.foreach(println)

  val pkl2 = Pickle.intoString[Tile](Tile.WALL)
  println(pkl2)
  println(Unpickle[Tile].fromString(pkl2).get)

  val pk3 = Pickle.intoString[Vector2](Vector2.UNIT)
  println(pk3)
  println(Unpickle[Vector2].fromString(pk3).get)

  val pk4 = Pickle.intoString[GameMap](GameMap(Array.fill[Tile](5, 5) { Tile.WALL} ))
  println(pk4)
  println(Unpickle[GameMap].fromString(pk4).get)

  val pk5 = Pickle.intoString[SimplexNoise](SimplexNoise())
  println(pk5)

  //  val pkl2 = Pickle.intoString[GameMap](GameMap(Array.ofDim[Tile](5, 5)))
//  println(pkl2)
//  println(Unpickle[GameMap].fromString(pkl2).get)

  private def persistJs(k: Symbol, value: js.Any) = persist(k, JSON.stringify(value))
  private def loadJs[T](k: Symbol): T = JSON.parse(load[String](k).get).asInstanceOf[T]

  private def store(p: Persist): Unit  = persist(p.key, p.persist())
  private def restore(p: Persist): Unit = p.restore(load[Map[Symbol, String]](p.key).get)

  val persistObjs = List(Knave, Simplex)

  def persistAll(): Unit = {
    if (!ok) return
    persistObjs.foreach(store)
  }

  def loadAll(): Unit = {
    persistObjs.foreach(restore)
  }

  def clearAll(): Unit = window.localStorage.clear()

  def hasSave: Boolean = load[Boolean](HAS_SAVE).getOrElse(false)
}
