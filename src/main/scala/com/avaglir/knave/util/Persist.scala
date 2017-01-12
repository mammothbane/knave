package com.avaglir.knave.util

trait Persist {
  def persist(): Map[Symbol, String]
  def restore(v: Map[Symbol, String])
  def key: Symbol
}
