package com.avaglir.knave.map

import com.avaglir.knave.util._
import org.scalajs.dom.document

import scala.collection.mutable

/**
  * Effectively translates between World/Chunk coordinates and Tile coordinates. Everything in this file is in
  * Chunk coordinates unless otherwise stated.
  */
object Overworld {
  private val PRELOAD = 3
  private val under = PRELOAD / 2
  private val over = PRELOAD - PRELOAD / 2
  val min = Vector2(under, under)
  val max = Vector2.UNIT[Int] * (World.DIMENS - over)

  private var location = Vector2.ZERO[Int]
  private var cached: Map[Vector2[Int], Chunk] = surrounding(location).map { loc => (loc, Chunk(loc)) }.toMap

  private implicit class vecExt(v: Vector2[Int]) {
    def chunkCoords = v / Chunk.DIMENS
    def tileCoords = v * Chunk.DIMENS
  }

  /**
    * Convert from tile coords to chunk coordinates.
    */
  private def chunkCoords(v: Vector2[Int]): Vector2[Int] = v.chunkCoords
  private def cacheExtent: (Vector2[Int], Vector2[Int]) = (location.clamp(min, max) - Vector2(under, under), location.clamp(min, max) + Vector2(over, over))

  /**
    * Return the chunk coordinates of the surrounding chunks.
    */
  private def surrounding(v: Vector2[Int]): List[Vector2[Int]] = {
    val chunk = v.chunkCoords.clamp(min, max)
//    println(s"surrounding ${v.chunkCoords}: min: $min, max: $max, val: $chunk")

    val out = (chunk.x - under until chunk.x + over).cartesianProduct(chunk.y - under until chunk.y + over).map { case (x, y) => Vector2(x, y) }.toList
//    println(s"chunks: $out")
    out
  }

  /**
    * Set a new tile coordinate center.
    */
  def setCenter(newCenter: Vector2[Int]): Unit = {
    val vec = newCenter.chunkCoords
    if (vec == location) return

    val (preserved, load) = surrounding(newCenter).partition { cached contains _ }
    cached = (load.map { loc => (loc, Chunk(loc)) } ++ preserved.map { loc => (loc, cached(loc)) }).toMap
    location = vec

    println(s"center updated: $vec")
    load.foreach { key => println(s"$key loaded")}
  }

  private def chunkContaining(location: Vector2[Int]): Option[Chunk] = cached.get(location.chunkCoords)

  object debug {
    val tile = document.getElementById("tile")
    val chunk = document.getElementById("chunk")
    val cacheMin = document.getElementById("cache-min")
    val cacheMax = document.getElementById("cache-max")
    val camera = document.getElementById("camera")
  }

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
        val targetChunk = chunkContaining(loc).get
        val cnkCoords = loc - targetChunk.location.tileCoords

        out(vec.x)(vec.y) = targetChunk(cnkCoords)
      }
    }

    debug.cacheMin.textContent = s"Cache Min: ${cacheMin.tileCoords}"
    debug.cacheMax.textContent = s"Cache Max: ${cacheMax.tileCoords}"
    debug.camera.textContent = s"Camera: $cam"

    out
  }

  val TILE_MAX = Vector2.UNIT[Int] * Chunk.TILE_DIMENS

  /**
    * Build the set of seen locations at the given camera location and window dimensions.
    */
  def seen(camera: Vector2[Int], dimens: Vector2[Int]): Set[Vector2[Int]] = {
    val (cacheMin, cacheMax) = cacheExtent
    val cam = camera.clamp(cacheMin.tileCoords, cacheMax.tileCoords)

    val screenOrigin = cam - dimens/2 // in tile coordinates

    val seenChunks = mutable.Set.empty[Chunk]
    for (x <- 0 until dimens.x; y <- 0 until dimens.y) {
      val vec = Vector2(x, y)
      if (vec.componentsClamped(TILE_MAX)) {
        seenChunks.add(chunkContaining(screenOrigin + vec).get)
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
    chunkContaining(vec) match {
      case Some(chunk) =>
        val inChunk = vec - chunk.location.tileCoords
        chunk.view(inChunk)
      case None =>
    }
  }
}
