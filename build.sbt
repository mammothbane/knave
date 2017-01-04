enablePlugins(ScalaJSPlugin)

name := "Knave"
scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1"
)

jsDependencies += "org.webjars.npm" % "rot-js" % "0.6.2" / "rot.js"

skip in packageJSDependencies := false
