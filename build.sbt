import play.Project._

organization := "io.sphere.shop"

name := "sphere-donut"

version := "1.0-SNAPSHOT"

playJavaSettings

libraryDependencies ++= Seq(
  javaCore,
  javaJdbc,
  "io.sphere" %% "sphere-play-sdk" % "0.67.0" withSources(),
  "org.jsoup" % "jsoup" % "1.7.1",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)

lessEntryPoints := baseDirectory.value / "app" / "assets" / "stylesheets" * "*.less"

templatesImport ++= Seq(
  "forms._",
  "io.sphere.client.model._",
  "io.sphere.client.shop.model._"
)

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")
