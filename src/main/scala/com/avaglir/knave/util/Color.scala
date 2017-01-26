package com.avaglir.knave.util

sealed abstract class Color {
  def red: Int
  def green: Int
  def blue: Int

  def hue: UnitClampedFloat
  def saturation: UnitClampedFloat
  def luminance: UnitClampedFloat

  lazy val hex: String = "#" + bytesToHex(Array(red.toByte, green.toByte, blue.toByte))

  def hsl = HSL(hue, saturation, luminance)
  def rgb = RGB(red, green, blue)

  def darker = HSL(hue, saturation, UnitClampedFloat(luminance.value/2))
  def lighter = HSL(hue, saturation, UnitClampedFloat((luminance.value + 1f)/2))

  def desaturated = HSL(hue, UnitClampedFloat(saturation.value/2), luminance)
  def saturated = HSL(hue, UnitClampedFloat((saturation.value + 1f)/2), luminance)
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
      (0f.unitClamped, 0f.unitClamped, lum.unitClamped)
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

  override lazy val hashCode: Int = 0 << 30 | red & 0xff << 16 | green & 0xff << 8 | blue & 0xff
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

  override lazy val hashCode: Int = 0x01 << 30 | ((hue.value * 0x3f).toInt & 0xff) << 20 | ((saturation.value * 0x3f).toInt & 0x3f) << 10 | ((luminance.value * 0x3f).toInt & 0x3f)
}

object Color {
  val WHITE = RGB(0xff, 0xff, 0xff)
  val BLACK = RGB(0, 0, 0)
  val RED = RGB(0xff, 0, 0)
  val GREEN = RGB(0, 0xff, 0)
  val BLUE = RGB(0, 0, 0xff)
  val YELLOW = RGB(0xff, 0xff, 0)
  val CYAN = RGB(0, 0xff, 0xff)
  val MAGENTA = RGB(0xff, 0, 0xff)

  private val matchRegex = "#([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})".r

  def apply(s: String): RGB = {
    val matchRegex(r, g, b) = s
    val (red :: green :: blue :: _) = List(r, g, b).map(Integer.parseInt(_, 16) % 256)
    println(red, green, blue)

    RGB(red, green, blue)
  }

  val BROWN = Color("#89764e")
}
