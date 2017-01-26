package com.avaglir.knave.map

import com.avaglir.knave.Knave
import com.avaglir.knave.util._

import scala.collection.mutable

/**
  * Effectively translates between World/Chunk coordinates and Tile coordinates. Everything in this file is in
  * Chunk coordinates unless otherwise stated.
  */
object Overworld extends Persist {
  private val PRELOAD = 3
  private val under = PRELOAD / 2
  private val over = PRELOAD - PRELOAD / 2
  val min = Vector2(under, under)
  val max = Vector2.UNIT[Int] * (World.DIMENS - over)

  private var location = Vector2.ZERO[Int]
  private val cached: Array[Array[Chunk]] = Array.ofDim[Chunk](PRELOAD, PRELOAD)
  setCenter(location, force = true)

  private implicit class vecExt(v: Vector2[Int]) {
    def chunkCoords = v / Chunk.DIMENS
    def tileCoords = v * Chunk.DIMENS
  }

  private def cacheExtent(v: Vector2[Int] = location): (Vector2[Int], Vector2[Int]) = (v.clamp(min, max) - Vector2(under, under), v.clamp(min, max) + Vector2(over, over))

  /**
    * Set a new tile coordinate center.
    */
  def setCenter(newCenter: Vector2[Int], force: Boolean = false): Unit = {
    val vec = newCenter.chunkCoords
    if (!force && vec == location) return

    val (cacheMin, cacheMax) = cacheExtent()
    val (newCacheMin, newCacheMax) = cacheExtent(vec)

    val mn = Vector2(maxOf(cacheMin.x, newCacheMin.x), maxOf(cacheMin.y, newCacheMin.y))
    val mx = Vector2(maxOf(cacheMax.x, newCacheMax.x), maxOf(cacheMax.y, newCacheMax.y))

    val preserve = mutable.Map.empty[Vector2[Int], Chunk]

    if (!force) {
      for (x <- mn.x until mx.x; y <- mn.y until mx.y) {
        preserve += ((Vector2(x, y), cached(x)(y)))
      }
    }

    for (x <- 0 until PRELOAD; y <- 0 until PRELOAD) {
      preserve.get(Vector2(x, y)) match {
        case Some(chunk) => cached(x)(y) = chunk
        case None =>
          println(s"loaded $x, $y")
          cached(x)(y) = Chunk(newCacheMin + Vector2(x, y))
      }
    }

    location = vec
    println(s"center updated: $vec")
  }

  @inline def cachedChunkOrigin: Vector2[Int] = location.clamp(min, max) - min
  @inline private def chunkContaining(loc: Vector2[Int]): Chunk = {
    val l = loc.chunkCoords - cachedChunkOrigin
    cached(l)
  }

  /**
    * Build a 2d array representing a portion of the map.
    * @param camera Location of the camera (center) in Tile coordinates.
    * @param dimens Dimensions of the matrix to return.
    * @return
    */
  def render(camera: Vector2[Int], dimens: Vector2[Int]): Array[Array[Tile]] = {
    val (cacheMin, cacheMax) = cacheExtent()
    val cam = camera.clamp(cacheMin.tileCoords, cacheMax.tileCoords)

    val out = Array.ofDim[Tile](dimens.x, dimens.y)
    val screenOrigin = (cam - dimens/2).clamp(Vector2.ZERO[Int], Vector2.UNIT[Int] * Chunk.TILE_DIMENS) // in tile coordinates

    (0 until dimens.x).cartesianProduct(0 until dimens.y).map(Vector2.apply[Int]).foreach { vec =>
      val loc = screenOrigin + vec // the actual tile we're retrieving, in tile coordinates
      if (!loc.componentsClamped(Vector2.UNIT[Int] * Chunk.TILE_DIMENS)) {
        out(vec.x)(vec.y) = Tile.WATER
      } else {
        val targetChunk = chunkContaining(loc)
        val cnkCoords = loc - targetChunk.location.tileCoords

        out(vec.x)(vec.y) = targetChunk(cnkCoords)
      }
    }

    Knave.displays('status).drawText(Vector2(1, 4), "Overworld")
    Knave.displays('status).drawText(Vector2(1, 5), s"Cache")
    Knave.displays('status).drawText(Vector2(1, 6), s"  ${cacheMin.tileCoords}", trim = false)
    Knave.displays('status).drawText(Vector2(1, 7), s"  ${cacheMax.tileCoords}", trim = false)
    Knave.displays('status).drawText(Vector2(1, 8), s"Camera: $cam")

    out
  }

  val TILE_MAX = Vector2.UNIT[Int] * Chunk.TILE_DIMENS

  /**
    * Build the set of seen locations at the given camera location and window dimensions.
    */
  def seen(camera: Vector2[Int], dimens: Vector2[Int]): Set[Vector2[Int]] = {
    val (cacheMin, cacheMax) = cacheExtent()
    val cam = camera.clamp(cacheMin.tileCoords, cacheMax.tileCoords)

    val screenOrigin = cam - dimens/2 // in tile coordinates

    val seenChunks = mutable.Set.empty[Chunk]
    for (x <- 0 until dimens.x; y <- 0 until dimens.y) {
      val vec = Vector2(x, y)
      if (vec.componentsClamped(TILE_MAX)) {
        seenChunks.add(chunkContaining(screenOrigin + vec))
      }
    }

    seenChunks.toSet.
      flatMap { (chunk: Chunk) =>
        val root = chunk.location
        chunk.remembered.map { _ + root.tileCoords - screenOrigin }
      }.
      filter { _.componentsClamped(dimens) }
  }

  /**
    * Mark the given (Tile coordinate) locations as seen in their respective chunks.
    */
  def markSeen(vecs: Vector2[Int]*): Unit = vecs.foreach { vec =>
    val chunk = chunkContaining(vec)
    val inChunk = vec - chunk.location.tileCoords
    chunk.view(inChunk)
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
