package com.avaglir.knave.util

/**
  * Created by mammothbane on 1/10/2017.
  */
import scala.language.experimental.macros
object Macros {
  import scala.reflect.macros.whitebox
  def persist_impl[T: c.WeakTypeTag](c: whitebox.Context)(k: c.Expr[Symbol], value: c.Expr[T]): c.Expr[Unit] = {
    import c.universe._
    c.Expr[Unit](q"""org.scalajs.dom.window.localstorage.setItem($k.name, prickle.Pickle.intoString[${weakTypeOf[T]}]($value))""")
  }

  def persist[T](k: Symbol, value: T) = macro Macros.persist_impl[T]
}
