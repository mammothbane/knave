enablePlugins(ScalaJSPlugin)

name := "Knave"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "com.lihaoyi" %%% "upickle" % "0.4.3"
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

skip in packageJSDependencies := false
jsDependencies ++= Seq(
  ProvidedJS / "rot.min.js"
)

mainClass in Compile := Some("com.avaglir.knave.Knave")

scalacOptions ++= Seq(
  "-language:implicitConversions"
)
