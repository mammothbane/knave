package com.avaglir.knave.entities

import com.avaglir.knave.util._

trait GameObject {
    def repr: RenderTile

    def displayPriority: Int = 0

    def draw(display: Display, loc: IntVec) = repr.draw(display, loc)
}
