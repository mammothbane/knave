package com.avaglir.knave.items

import com.avaglir.knave.entities.{Entity, GameObject}

abstract class Item(var owner: Entity) extends GameObject {
  def weight: Int = 1
  def name: String
  def basePrice: Int
}
