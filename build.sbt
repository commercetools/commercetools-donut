import play.Project._

organization := "io.sphere.shop"

name := "sphere-donut"

version := "1.0-SNAPSHOT"

playJavaSettings

libraryDependencies ++= Seq(
  javaCore,
  javaJdbc,
  "io.sphere" %% "sphere-play-sdk" % "0.67.0" withSources(),
  "com.novocode" % "junit-interface" % "0.11" % "test,it",
  "org.assertj" % "assertj-core" % "2.0.0" % "test,it", // change to 3.0.0 with Java 8
  play.Project.component("play-test") % "it"
)

lessEntryPoints := baseDirectory.value / "app" / "assets" / "stylesheets" * "*.less"

templatesImport ++= Seq(
  "forms._",
  "io.sphere.client.model._",
  "io.sphere.client.shop.model._"
)

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

javaSource in IntegrationTest := baseDirectory.value / "it"

resourceDirectory in IntegrationTest := baseDirectory.value / "it/resources"

lazy val root = (project in file(".")).configs(IntegrationTest).settings(Defaults.itSettings:_*)