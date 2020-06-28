package com.avaglir.knave.util

import scala.collection.mutable

class Lru[K, V](val maxElems: Int, compute: K => V, val expiration: Option[Int], initial: K*) {
    private var lastEvict = System.currentTimeMillis
    private val maxEvictFreq = 100 // only run evictions every 100ms

    private val lru = mutable.Map[K, Long](initial.map {
        (_, System.currentTimeMillis)
    }: _*)
    private val vals = mutable.Map(initial.map { elem => (elem, compute(elem)) }: _*)

    def apply(k: K): V = {
        val now = System.currentTimeMillis
        if (expiration.isDefined && now - lastEvict > maxEvictFreq) evict()

        if (vals contains k) {
            lru(k) = now
            vals(k)
        } else {
            if (vals.size == maxElems) {
                val evictK = lru.minBy { case (_, time) => time }._1
                vals remove evictK
                lru remove evictK
            }

            val upd = compute(k)
            vals(k) = upd
            lru(k) = now
            upd
        }
    }

    def invalidate(k: K): Option[V] = {
        lru remove k
        vals remove k
    }

    private def evict(): Unit = {
        val now = System.currentTimeMillis
        lastEvict = now

        lru.filter { case (_, time) => now - time > expiration.get }.unzip._1.foreach { key =>
            lru remove key
            vals remove key
        }
    }
}
