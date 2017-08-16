package com.avaglir.knave

import com.avaglir.util.color.Color

import scala.annotation.tailrec
import scala.collection.{GenTraversable, mutable}

package object util {
  def seededRandom(implicit seed: RandomSeed): rot.RNG = {
    val rand = rot.RNG.clone()
    rand.setSeed(seed.value)
    rand
  }

  val hexArray = "0123456789abcdef".toCharArray
  @inline def bytesToHex(bytes: Array[Byte], debug: Boolean = false): String = {
    val hexChars = new Array[Char](bytes.length * 2)
    for (j <- bytes.indices) {
      val v = bytes(j) & 0xff
      hexChars(j * 2)     = hexArray(v >>> 4)
      hexChars(j * 2 + 1) = hexArray(v & 0x0f)
    }
    new String(hexChars)
  }

  implicit class strCtxExt(s: StringContext) {
    def c(args: Any*): Color = Color(s.parts.mkString)
  }

  private final val colorRegex = "%c{#[0-9a-f]{6}}%b{#[0-9a-f]{6}}".r
  implicit class strExt(s: String) {
    def colorize(fg: Color = Color.WHITE, bg: Color = Color.BLACK): String = s"%c{${fg.hex}}%b{${bg.hex}}$s%c{${Color.WHITE.hex}}%b{${Color.BLACK.hex}}"
    def decolorize: String = colorRegex.replaceAllIn(s, "")
    def titleCase: String = s match {
      case "" => s
      case x if x.length == 1 => x.toUpperCase
      case _ => s(0).toUpper + s.substring(1)
    }
  }
}
