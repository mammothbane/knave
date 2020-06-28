package com.avaglir.knave.util

import rot.RNG

trait Generator[T] {
  def generate(rng: RNG): T
}
