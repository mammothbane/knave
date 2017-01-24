package com.avaglir.knave.properties

import com.avaglir.knave.entities.Entity
import com.avaglir.knave.util._

class Fighter(
                parent: Entity
             ) extends Property[Entity](parent) {
  override def name: String = "fighter"
  override def message[T, U](message: Message[T, U]): Unit = message match {
    case Message('stats, _, fn) =>
    case Message('combat, Some(enemy), fn) =>
    case _ =>
  }
}

object Fighter extends Persist with Random {
  def stats = Message('stats, None, None)
  def combat(other: Fighter) = Message('combat, Some(other), None)

  import com.avaglir.knave.util.storage.Pickling._
  import prickle._
  override def persist(): Map[Symbol, String] = Map(
    'random -> Pickle.intoString(seed)
  )
  override def restore(v: Map[Symbol, String]): Unit = {
    this.setSeed(Unpickle[Double].fromString(v('random)).get)
  }
  override def key: Symbol = 'fighter
}