package com.avaglir.knave.properties

case class Message[T, U](name: Symbol, data: Option[T], ret: Option[U => Unit])
