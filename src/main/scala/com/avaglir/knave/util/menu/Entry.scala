package com.avaglir.knave.util.menu

import com.avaglir.knave.util.Color

/**
 * Created by mammothbane on 1/11/2017.
 */
case class Entry(
    name: String,
    result: Symbol,
    var enabled: Boolean = true,
    disabledColor: Color = Color.WHITE.darker,
  )
