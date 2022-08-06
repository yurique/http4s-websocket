val Http4sVersion  = "0.23.14"
val LogbackVersion = "1.2.11"

lazy val root = (project in file("."))
  .settings(
    organization := "com.example",
    name         := "http4s-websocket",
    version      := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.8",
    libraryDependencies ++= Seq(
      "org.http4s"    %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"    %% "http4s-dsl"          % Http4sVersion,
      "ch.qos.logback" % "logback-classic"     % LogbackVersion % Runtime,
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.2" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
  )
