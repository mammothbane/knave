lazy val main = (project in file(".")).
  enablePlugins(ScalaJSPlugin).
  settings(commonSettings: _*).
  settings(
    name := "Knave",
    mainClass in Compile := Some("com.avaglir.knave.Knave"),
    scalacOptions ++= Seq(
      "-language:implicitConversions",
      "-language:reflectiveCalls",
      "-language:higherKinds"
    ),
    skip in packageJSDependencies := false,
    jsDependencies ++= Seq(
      ProvidedJS / "rot.min.js"
    ),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.1",
      "com.github.benhutchison" %%% "prickle" % "1.1.13"
    ),
    resolvers ++= Seq(
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    )
  ).dependsOn(macros)

lazy val macros = (project in file("macros")).
  settings(commonSettings: _*).
  settings(
    scalacOptions ++= Seq(
      "-language:experimental.macros"
    ),
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % "2.12.1"
    )
  )

lazy val commonSettings = Seq(
  scalaVersion := "2.12.1",
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  resolvers += Resolver.sonatypeRepo("releases")
)
