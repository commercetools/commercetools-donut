import play.Project._

organization := "io.sphere.shop"

name := "sphere-donut"

version := "1.0-SNAPSHOT"

playJavaSettings

libraryDependencies ++= Seq(
  javaCore,
  javaJdbc,
  "io.sphere" %% "sphere-play-sdk" % "0.67.0" withSources(),
  "org.assertj" % "assertj-core" % "2.0.0" % "test" // change to 3.0.0 with Java 8
)

lessEntryPoints := baseDirectory.value / "app" / "assets" / "stylesheets" * "*.less"

templatesImport ++= Seq(
  "forms._",
  "io.sphere.client.model._",
  "io.sphere.client.shop.model._"
)

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")
