package com.avaglir.knave.util

import com.avaglir.knave.Knave
import com.avaglir.knave.util.storage.Macros._
import org.scalajs.dom.window

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.util.{Failure, Success, Try}

package object storage {
  private val KNAVE_NAMESPACE = "KNAVE"
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

  val RAND_STATE = 'rand_state
  val RAND_SEED = 'rand_seed

  private def persistJs(k: Symbol, value: js.Any) = persist(k, JSON.stringify(value))
  private def loadJs[T](k: Symbol): T = JSON.parse(load[String](k).get).asInstanceOf[T]

  def persistAll(): Unit = {
    if (!ok) return
    persistJs(RAND_STATE, Knave.random.getState())
    persistJs(RAND_SEED, Knave.random.getSeed())

  }

  def loadAll(): Unit = {
    Knave.random.setState(loadJs(RAND_STATE))
    Knave.random.setSeed(loadJs(RAND_SEED))
  }

  def clearAll(): Unit = {
    println("deleting local storage")
    window.localStorage.clear()
  }
}
