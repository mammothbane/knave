package com.avaglir.knave.util

/**
 * A potentially-significant optimization. Work to do after the frame finishes in preparation for the next one.
 */
trait PostFrame {
  def doPostFrame(): Unit
}
