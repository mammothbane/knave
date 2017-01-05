package com.avaglir.knave.util

case class Color(r: Int, g: Int, b: Int) {
  def hex: String = f"#$r%02x$g%02x$b%02x"
}

object Color {
  val WHITE = Color(0xff, 0xff, 0xff)
  val BLACK = Color(0, 0, 0)
  val RED = Color(0xff, 0, 0)
  val GREEN = Color(0, 0xff, 0)
  val BLUE = Color(0, 0, 0xff)

  private val matchRegex = "#([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})".r

  def apply(s: String): Color = {
    val matchRegex(r, g, b) = s
    Color(Integer.parseInt(r, 16).toByte, Integer.parseInt(g, 16).toByte, Integer.parseInt(b, 16).toByte)
  }
}
