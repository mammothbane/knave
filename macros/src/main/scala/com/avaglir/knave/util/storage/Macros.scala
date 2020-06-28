package com.avaglir.knave.util.storage

import scala.util.Try

object Macros {
  import scala.reflect.macros.whitebox
  def persist_impl[T: c.WeakTypeTag](c: whitebox.Context)(k: c.Expr[Symbol], value: c.Expr[T]): c.Expr[Unit] = {
    import c.universe._
    c.Expr[Unit](q"""org.scalajs.dom.window.localStorage.setItem($k.name, prickle.Pickle.intoString[${weakTypeOf[T]}]($value))""")
  }

  def load_impl[T: c.WeakTypeTag](c: whitebox.Context)(k: c.Expr[Symbol]): c.Expr[Try[T]] = {
    import c.universe._
    c.Expr[util.Try[T]](q"""prickle.Unpickle[${weakTypeOf[T]}].fromString(org.scalajs.dom.window.localStorage.getItem($k.name))""")
  }

  private[storage] def persist[T](k: Symbol, value: T): Unit = macro persist_impl[T]
  private[storage] def load[T](k: Symbol): Try[T] = macro load_impl[T]
}
