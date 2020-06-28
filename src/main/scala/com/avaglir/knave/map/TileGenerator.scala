package com.avaglir.knave.map

/**
 * Created by mammothbane on 1/10/2017.
 */
trait TileGenerator {
    def generate(width: Int, height: Int): Array[Array[Tile]]
}
