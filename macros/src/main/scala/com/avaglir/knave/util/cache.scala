package com.avaglir.knave.util

import scala.annotation._

@compileTimeOnly("enable macro paradise to expand macro annotations")
class cache extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CacheImpl.impl
}

private object CacheImpl {
  import scala.reflect.macros._
  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    val out = annottees.map { _.tree }.toList match {
      case q"def $methodName(...$vals): $returnType = { ..$body }" :: Nil =>
        val elems = vals.asInstanceOf[List[List[ValDef]]]
        val call = elems.map { elem => elem.map { vl => vl.name } }

        q"""object $methodName {
              private def _run(...$vals): $returnType = { ..$body }
              private val cacher = new com.avaglir.knave.util.Lru(25, _run, None)
              def apply(...$vals): $returnType = cacher(...$call)
            }"""
      case _ => c.abort(c.enclosingPosition, "Annotation @cache match error.")
    }

    c.Expr[Any](out)
  }
}