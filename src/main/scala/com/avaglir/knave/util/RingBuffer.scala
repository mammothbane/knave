package com.avaglir.knave.util

import scala.reflect.ClassTag

class RingBuffer[T: ClassTag](capacity: Int) {
  private val buf  = new Array[T](capacity)
  private var head = 0
  private var tail = 0

  def isEmpty: Boolean  = head == tail
  def nonEmpty: Boolean = head != tail
  def isFull: Boolean   = (tail - head + capacity) % capacity == 1

  def size: Int = length

  def put(t: T): Boolean = {
    if (isFull) return false
    buf(head) = t
    head += 1
    true
  }

  def get: Option[T] = {
    if (isEmpty) return None

    val ret = buf(tail)
    tail = (tail + 1) % capacity
    Some(ret)
  }

  def length: Int =
    if (head >= tail) {
      (head - tail + capacity) % capacity
    }
    else {
      size - (tail - head)
    }
}
