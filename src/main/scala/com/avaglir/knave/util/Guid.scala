package com.avaglir.knave.util

import java.util.UUID

trait Guid {
  private var _guid   = UUID.randomUUID()
  def guid            = _guid
  def guid_=(g: UUID) = _guid = g
}
