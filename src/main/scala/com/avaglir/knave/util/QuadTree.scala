package com.avaglir.knave.util

import collection.mutable

/**
  * Adapted from: http://www.cs.trinity.edu/~mlewis/CSCI1321-F11/Code/src/util/Quadtree.scala
  * @param b
  * @tparam A
  */
class QuadTree[A](dimen: Vector2)(implicit b: A => ((Int) => Double)) {
  private val MAX_OBJECTS = 3

  private class Node(cx: Double, cy: Double, sx: Double, sy: Double) {
    val objects: mutable.ListBuffer[A] = mutable.ListBuffer[A]()
    var children: Seq[Node] = Seq()

    def whichChild(obj: A): Int = (if (obj(0) > cx) 1 else 0) + (if (obj(1) > cy) 2 else 0)

    def makeChildren() {
      children = Seq(
        new Node(cx-sx/4, cy-sy/4, sx/2, sy/2),
        new Node(cx+sx/4, cy-sy/4, sx/2, sy/2),
        new Node(cx-sx/4, cy+sy/4, sx/2, sy/2),
        new Node(cx+sx/4, cy+sy/4, sx/2, sy/2)
      )
    }

    def overlap(obj: A, radius: Double): Boolean = {
      obj(0) - radius < cx + sx / 2 && obj(0) + radius > cx - sx / 2 &&
        obj(1) - radius < cy + sy / 2 && obj(1) +radius > cy - sy / 2
    }

    def add(a: A): Unit = {
      if (children.nonEmpty) {
        children(whichChild(a)) add a
        return
      }

      if (objects.length < MAX_OBJECTS) {
        objects += a
        return
      }

      makeChildren()
      objects.foreach { elem => children(whichChild(a)).add(elem) }
      objects.clear

      children(whichChild(a)) add a
    }

    def search(a: A, radius: Double): List[A] =
      if (children.isEmpty) objects.filter(o => distance(o, a) < radius).toList
      else children.filter { _.overlap(a, radius) }.flatMap { _.search(a, radius) }.toList
  }

  private val root = new Node(dimen.half.x, dimen.half.y, dimen.x, dimen.y)

  def add(a: A) = root add a
  def search(a: A, radius: Double) = root search (a, radius)

  private def distance(a: A, b: A): Double = {
    val dx = a(0) - b(0)
    val dy = a(1) - b(1)
    math.sqrt(dx*dx + dy*dy)
  }
}
