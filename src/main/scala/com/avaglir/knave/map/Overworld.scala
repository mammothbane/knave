package com.avaglir.knave.map

import com.avaglir.knave.util._

object Overworld extends Persist {
  println(surrounding(Vector2.ZERO[Int]))

  private val PRELOAD = 5
  private val under = PRELOAD / 2
  private val over = PRELOAD - PRELOAD / 2

  private var location = Vector2.ZERO[Int]
  private var cached: Map[Vector2[Int], Chunk] = surrounding(location).map { loc => (loc, Chunk(loc)) }.toMap

  /**
    * Retrieve the coordinates of the top-left corner of the chunk containing the given vector.
    */
  private def chunkCoords(v: Vector2[Int]): Vector2[Int] = v.map { component => math.round(component.toFloat/Chunk.DIMENS)*Chunk.DIMENS }
  private def cacheExtent: (Vector2[Int], Vector2[Int]) = (location - Vector2(under * Chunk.DIMENS, under*Chunk.DIMENS), location + Vector2(over * Chunk.DIMENS, over * Chunk.DIMENS))

  def surrounding(v: Vector2[Int]): List[Vector2[Int]] = {
    val chunk = chunkCoords(v)

    val vec = chunk.clamp(Vector2.UNIT[Int] * under * Chunk.DIMENS, Vector2.UNIT[Int] * (World.DIMENS - over)*Chunk.DIMENS)
    println(vec)

    (vec.x - under until vec.x + over).cartesianProduct(vec.y - under until vec.y + over).map {
      case (x: Int, y: Int) => Vector2(x, y)*Chunk.DIMENS
    }.toList
  }

  def setCenter(newCenter: Vector2[Int]): Unit = {
    val vec = chunkCoords(newCenter)
    if (vec == location) return

    val (preserved, load) = surrounding(newCenter).partition { cached contains _ }
    cached = (load.map { loc => (loc, Chunk(loc)) } ++ preserved.map { loc => (loc, cached(loc)) }).toMap
    location = newCenter
  }

  private def chunkContaining(location: Vector2[Int]): Option[Chunk] = cached.get(chunkCoords(location))

  def render(camera: Vector2[Int], dimens: Vector2[Int]): Array[Array[Tile]] = {
    val (cacheMin, cacheMax) = cacheExtent
    val cam = camera.clamp(cacheMin, cacheMax)

    val out = Array.ofDim[Tile](dimens.x, dimens.y)
    val tl = cam - dimens/2

    (0 until dimens.x).cartesianProduct(0 until dimens.y).map(Vector2.apply[Int]).foreach { vec =>
      val loc = tl + vec
      val rootCoords = chunkCoords(vec)
      val targetChunk = chunkContaining(vec).get
      val cnkCoords = loc - rootCoords

      out(vec.x)(vec.y) = targetChunk(cnkCoords)
    }

    out
  }

  import com.avaglir.knave.util.storage.Pickling._
  import prickle._
  override def persist(): Map[Symbol, String] = Map(
    'location -> Pickle.intoString(location)
  )

  override def restore(v: Map[Symbol, String]): Unit = {
    location = Unpickle[Vector2[Int]].fromString(v('location)).get
  }
  override def key: Symbol = 'overworld
}
