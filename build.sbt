enablePlugins(ScalaJSPlugin)

name := "Knave"
scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "com.flowpowered" % "flow-noise" % "1.0.1-SNAPSHOT"
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

skip in packageJSDependencies := false
jsDependencies += ProvidedJS / "rot.min.js"
persistLauncher := true

mainClass in Compile := Some("com.avaglir.knave.Knave")
