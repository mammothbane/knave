package com.avaglir.knave.map

import com.avaglir.knave.util._

/**
  * Effectively translates between World/Chunk coordinates and Tile coordinates. Everything in this file is in
  * Chunk coordinates unless otherwise stated.
  */
object Overworld extends Persist {
  private val PRELOAD = 3
  private val under = PRELOAD / 2
  private val over = PRELOAD - PRELOAD / 2

  private var location = Vector2.ZERO[Int]
  private var cached: Map[Vector2[Int], Chunk] = surrounding(location).map { loc => (loc, Chunk(loc)) }.toMap

  private implicit class vecExt(v: Vector2[Int]) {
    def chunkCoords = v.map { component => math.round(component.toFloat/Chunk.DIMENS)*Chunk.DIMENS }
    def tileCoords = v / Chunk.DIMENS
  }

  /**
    * Convert from tile coords to chunk coordinates.
    */
  private def chunkCoords(v: Vector2[Int]): Vector2[Int] = v.chunkCoords
  private def cacheExtent: (Vector2[Int], Vector2[Int]) = (location.chunkCoords - Vector2(under, under), location.chunkCoords + Vector2(over, over))

  /**
    * Return the chunk coordinates of the surrounding chunks.
    */
  private def surrounding(v: Vector2[Int]): List[Vector2[Int]] = {
    val min = Vector2(under, under)
    val max = Vector2.UNIT[Int] * (World.DIMENS - over)
    val chunk = v.chunkCoords.clamp(min, max)

    (chunk.x - under until chunk.x + over).cartesianProduct(chunk.y - under until chunk.y + over).map { case (x, y) => Vector2(x, y) }.toList
  }

  /**
    * Set a new tile coordinate center.
    */
  def setCenter(newCenter: Vector2[Int]): Unit = {
    val vec = newCenter.chunkCoords
    if (vec == location) return

    val (preserved, load) = surrounding(newCenter).partition { cached contains _ }
    cached = (load.map { loc => (loc, Chunk(loc)) } ++ preserved.map { loc => (loc, cached(loc)) }).toMap
    location = newCenter
  }

  private def chunkContaining(location: Vector2[Int]): Option[Chunk] = cached.get(location.chunkCoords)

  /**
    * Build a 2d array representing a portion of the map.
    * @param camera Location of the camera (center) in Tile coordinates.
    * @param dimens Dimensions of the matrix to return.
    * @return
    */
  def render(camera: Vector2[Int], dimens: Vector2[Int]): Array[Array[Tile]] = {
    val (cacheMin, cacheMax) = cacheExtent
    val cam = camera.clamp(cacheMin.tileCoords, cacheMax.tileCoords)

    val out = Array.ofDim[Tile](dimens.x, dimens.y)
    val screenOrigin = cam - dimens/2 // in tile coordinates

    (0 until dimens.x).cartesianProduct(0 until dimens.y).map(Vector2.apply[Int]).foreach { vec =>
      val loc = screenOrigin + vec // the actual tile we're retrieving, in tile coordinates
      if (!loc.componentsClamped(Vector2.UNIT[Int] * Chunk.TILE_DIMENS)) {
        out(vec.x)(vec.y) = Tile.WATER
      } else {
        val rootCoords = loc.chunkCoords.tileCoords // the tile coordinates of this chunk
        val targetChunk = chunkContaining(loc).get
        val cnkCoords = loc - rootCoords

        out(vec.x)(vec.y) = targetChunk(cnkCoords)
      }
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
