package com.avaglir.knave.util

trait Persist {
  def persist(): Map[Symbol, String]
}
