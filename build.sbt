lazy val main = (project in file(".")).
    enablePlugins(ScalaJSPlugin).
    enablePlugins(JSDependenciesPlugin).
    settings(commonSettings: _*).
    settings(
        name := "Knave",
        mainClass in Compile := Some("com.avaglir.knave.Knave"),
        scalacOptions ++= Seq(
            "-language:implicitConversions",
            "-language:reflectiveCalls",
            "-language:higherKinds",
        ),
        skip in packageJSDependencies := false,
        jsDependencies ++= Seq(
            ProvidedJS / "rot.min.js",
            ProvidedJS / "nouns.js"
        ),
        libraryDependencies ++= Seq(
            "org.scala-js" %%% "scalajs-dom" % "1.0.0",
            "com.github.benhutchison" %%% "prickle" % "1.1.16"
        ),
        resolvers ++= Seq(
            "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
        )
    ).dependsOn(macros)

lazy val macros = (project in file("macros")).
    settings(commonSettings: _*).
    settings(
        scalacOptions ++= Seq(
            "-language:experimental.macros",
            "-Ymacro-annotations"
        ),
libraryDependencies ++= Seq(
            "org.scala-lang" % "scala-reflect" % "2.12.1"
        )
    )

lazy val commonSettings = Seq(
    scalaVersion := "2.13.3",
    resolvers += Resolver.sonatypeRepo("releases")
)
