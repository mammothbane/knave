package com.avaglir.knave.map

sealed abstract private[map] class IslandClass {
  def rank: Int

  def name: String

  def withModifiers(adj: Option[String], noun: Option[String]): String
}

private[map] trait IsleFormatter {
  this: { def name: String } =>

  def withModifiers(adj: Option[String], noun: Option[String]): String =
    if (adj.isEmpty && noun.isEmpty) s"Unknown $name"
    else if (adj.isEmpty) s"${noun.get} $name"
    else if (noun.isEmpty) s"The ${adj.get} $name"
    else s"The ${adj.get} $name of ${noun.get}"

}

object IslandClass {
  def all = List(Continent, Island, Isle, Atoll)

  object Continent extends IslandClass {
    override def rank: Int = 1

    override def name: String = "Continent"

    override def withModifiers(adj: Option[String], noun: Option[String]): String =
      "The " + (adj match {
        case Some(adjective) => s"$adjective "
        case None            => ""
      }) + name + (noun match {
        case Some(n) => s" of $n"
        case None    => ""
      })

  }

  object Island extends IslandClass with IsleFormatter {
    override val name = "Island"
    override val rank = 2
  }

  object Isle extends IslandClass with IsleFormatter {
    override val name = "Isle"
    override val rank = 3
  }

  object Atoll extends IslandClass {
    override val name = "Atoll"
    override val rank = 5

    override def withModifiers(adj: Option[String], noun: Option[String]): String =
      List(adj.getOrElse(""), noun.getOrElse(""), name).filter {
        _.length > 0
      }.mkString(" ")

  }

}
