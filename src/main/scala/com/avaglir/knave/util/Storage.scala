package com.avaglir.knave.util

import com.avaglir.knave.Knave
import org.scalajs.dom.window

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.util.{Failure, Success, Try}
import prickle._

import scala.language.experimental.macros

object Storage {
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

  private def persistJs(k: Symbol, value: js.Any) = Macros.persist(k, JSON.stringify(value))
  private def load[T](k: Symbol): T = Unpickle[T].fromString(window.localStorage.getItem(k.name)).get
  private def loadJs(k: Symbol): js.Dynamic = JSON.parse(load[String](k))

  def persistAll(): Unit = {
    if (!ok) return
    persistJs(RAND_STATE, Knave.random.getState())
    persistJs(RAND_SEED, Knave.random.getSeed())

  }

  def loadAll(): Unit = {
    Knave.random.setState(loadJs(RAND_STATE).asInstanceOf[rot.RNGState])
    Knave.random.setSeed(loadJs(RAND_SEED).asInstanceOf[Double])
  }

  def clearAll(): Unit = window.localStorage.clear()
}
