package com.avaglir.knave.properties

import com.avaglir.knave.entities.Entity
import com.avaglir.knave.skill.Skill

class Skilled(parent: Entity) extends Property[Entity](parent) {
  val skillMap = Skill.all.map(skill => (skill, skill.min)).toMap

  override def name: String = "skilled"
  override def message[U](message: Message[U]): Any = {}
}

object Skilled {
  def skillValue(s: Skill) = Message(Symbol("skill"), Some(s))
  def name                 = "skilled"
}
