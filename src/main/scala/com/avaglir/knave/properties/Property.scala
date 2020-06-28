package com.avaglir.knave.properties

import com.avaglir.knave.entities.Entity

abstract class Property[T <: Entity](val parent: T) {
  parent.register(this)

  def name: String
  def group: Option[Symbol] = None
  def message[U](message: Message[U]): Any
}
