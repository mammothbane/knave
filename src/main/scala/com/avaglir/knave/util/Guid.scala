package com.avaglir.knave.util

import java.util.UUID

trait Guid {
  private var _guid = UUID.randomUUID()

  def guid: UUID = _guid
  def guid_=(g: UUID): Unit = _guid = g
}
