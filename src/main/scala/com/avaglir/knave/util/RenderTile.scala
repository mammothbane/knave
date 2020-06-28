package com.avaglir.knave.util

case class RenderTile(
                         char: Char = ' ',
                         fg: Color = Color.WHITE,
                         bg: Color = Color.BLACK,
                         debug: Boolean = false
                     ) {
    def draw(display: Display, location: Vector2[Int]) = display.draw(location, char, fg, bg)
}
