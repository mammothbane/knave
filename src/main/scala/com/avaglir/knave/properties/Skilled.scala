package com.avaglir.knave.properties

import com.avaglir.knave.entities.Entity
import com.avaglir.knave.skill.Skill

/**
  * Created by mammothbane on 1/23/2017.
  */
class Skilled(parent: Entity) extends Property[Entity](parent) {
  val skillMap = Skill.all.map { skill => (skill, skill.min)}.toMap

  override def name: String = "skilled"
  override def message[U](message: Message[U]): Unit = {

  }
}

object Skilled {
  def skillValue(s: Skill, fn: Option[(Int) => Unit]): Message[Skill] = Message('skill, Some(s), fn)
}

