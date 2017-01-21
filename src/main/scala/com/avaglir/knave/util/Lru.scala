package com.avaglir.knave.util

import scala.collection.mutable

class Lru[K, V](val maxElems: Int, compute: K => V, val expiration: Option[Int], initial: K*) {
  private val lru = mutable.Map[K, Long](initial.map { (_, System.currentTimeMillis ) }: _*)
  private val vals = mutable.Map(initial.map { elem => (elem, compute(elem)) }: _*)

  def apply(k: K): V = {
    if (vals contains k) {
      lru(k) = System.currentTimeMillis
      vals(k)
    } else {
      if (vals.size == maxElems) {
        val evictK = lru.minBy { case (k, time) => time }._1
        vals remove evictK
        lru remove evictK
      }

      val upd = compute(k)
      vals(k) = upd
      upd
    }
  }

  def invalidate(k: K): Option[V] = vals remove k
}
