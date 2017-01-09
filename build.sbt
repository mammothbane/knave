enablePlugins(ScalaJSPlugin)

name := "Knave"
scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "org.json4s" %% "json4s-native" % "3.5.0",
  "org.scala-lang.modules" % "scala-pickling_2.11" % "0.10.1"
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

skip in packageJSDependencies := false
jsDependencies += ProvidedJS / "rot.min.js"
persistLauncher := true

mainClass in Compile := Some("com.avaglir.knave.Knave")

scalacOptions += "-language:implicitConversions"
