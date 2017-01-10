package com.avaglir.knave.util

import com.avaglir.knave.Knave
import org.scalajs.dom.window

import scala.util.{Success, Try}

object Storage {
  private val KNAVE_NAMESPACE = "KNAVE"
  private val STORAGE_TEST = "__TEST"
  lazy val ok = Try {
    window.localStorage.setItem(STORAGE_TEST, STORAGE_TEST)
    window.localStorage.removeItem(STORAGE_TEST)
  } match {
    case Success(_) => true
    case _ => false
  }

  def persistAll(): Unit = {
    import upickle.default._
    println(write(Knave.random.state))
    if (!ok) return
  }

}
