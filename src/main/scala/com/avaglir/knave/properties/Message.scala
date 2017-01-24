package com.avaglir.knave.properties

case class Message[T](name: Symbol, data: Option[T], ret: Option[T => Unit])
