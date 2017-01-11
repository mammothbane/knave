package com.avaglir.knave.input

sealed trait Action

object Action {
  case object INTERACT extends Action
  case object UP extends Action
  case object DOWN extends Action
  case object RIGHT extends Action
  case object LEFT extends Action
  case object SKIP extends Action
}
