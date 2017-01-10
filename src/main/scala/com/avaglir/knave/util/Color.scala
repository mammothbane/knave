package com.avaglir.knave.util

abstract class Color {
  def red: Int
  def green: Int
  def blue: Int

  def hue: UnitClampedFloat
  def saturation: UnitClampedFloat
  def luminance: UnitClampedFloat

  def hex: String = f"#$red%02x$green%02x$blue%02x"

  def hsl = HSL(hue, saturation, luminance)
  def rgb = RGB(red, green, blue)
}

case class RGB(red: Int, green: Int, blue: Int) extends Color {
  override lazy val (hue, saturation, luminance) = {
    val r = (red.toFloat / 255).clamp
    val g = (green.toFloat / 255).clamp
    val b = (blue.toFloat / 255).clamp

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)

    val lum = (max + min) / 2

    if (max == min) {
      (lum.unitClamped, 0f.unitClamped, 0f.unitClamped)
    } else {
      val d = max - min
      val sat = if (lum > 0.5) d / (2 - max - min) else d / (max + min)

      val hue = (max match {
        case x if x == r => (g - b) / d + (if (g < b) 6 else 0)
        case x if x == g => (b - r) / d + 2
        case x if x == b => (r - g) / d + 4
      })/6

      (hue.unitClamped, sat.unitClamped, lum.unitClamped)
    }
  }
}

case class HSL(hue: UnitClampedFloat, saturation: UnitClampedFloat, luminance: UnitClampedFloat) extends Color {
  lazy val (red, green, blue) = {
    val lumT = (luminance * 255).toInt

    if (saturation.value == 0) (lumT, lumT, lumT) else {
      val q = if (luminance < 0.5) luminance * (1 + saturation) else (luminance + saturation) - luminance * saturation
      val p = 2 * luminance - q

      def hue2rgb(t: Float): Int = {
        val t2 = if (t < 0) t + 1
          else if (t > 1) t - 1
          else t

        val out =
          if (t2 < 1f/6) p + (q - p) * 6 * t2
          else if (t2 < 0.5) q
          else if (t2 < 2f/3) p + (q - p) * (2f/3 - t2) * 6
          else p

        (out * 255).toInt
      }

      (hue2rgb(hue + (1f/3)), hue2rgb(hue), hue2rgb(hue - (1f/3)))
    }
  }
}

object Color {
  val WHITE = RGB(0xff, 0xff, 0xff)
  val BLACK = RGB(0, 0, 0)
  val RED = RGB(0xff, 0, 0)
  val GREEN = RGB(0, 0xff, 0)
  val BLUE = RGB(0, 0, 0xff)

  private val matchRegex = "#([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})".r

  def apply(s: String): RGB = {
    val matchRegex(r, g, b) = s
    RGB(Integer.parseInt(r, 16).toByte, Integer.parseInt(g, 16).toByte, Integer.parseInt(b, 16).toByte)
  }
}
