enablePlugins(ScalaJSPlugin)

name := "Knave"
scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1"
)

skip in packageJSDependencies := false
jsDependencies += ProvidedJS / "rot.min.js"
persistLauncher := true

mainClass in Compile := Some("com.avaglir.knave.Knave")
