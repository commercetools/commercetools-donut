import play.Project._

organization := "io.sphere.shop"

name := "sphere-donut"

version := "1.0-SNAPSHOT"

playJavaSettings

libraryDependencies ++= Seq(
  javaCore,
  javaJdbc,
  "io.sphere" %% "sphere-play-sdk" % "0.67.0" withSources(),
  "io.sphere.sdk.jvm" % "sphere-models" % "1.0.0-M16" withSources(),
  "io.sphere.sdk.jvm" % "sphere-java-client-apache-async" % "1.0.0-M16" withSources(),
  "org.apache.httpcomponents" % "httpasyncclient" % "4.0.2",
  "io.sphere.sdk.jvm" % "sphere-convenience" % "1.0.0-M16" withSources(),
  "io.sphere.sdk.jvm" %% "sphere-play-2_2-java-client" % "1.0.0-M16" withSources(),
  "com.novocode" % "junit-interface" % "0.11" % "test,it",
  "org.assertj" % "assertj-core" % "2.0.0" % "test,it", // change to 3.0.0 with Java 8
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.0",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.6.0",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.0",
  "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % "2.6.0",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.6.0",
  play.Project.component("play-test") % "it"
)

lessEntryPoints := baseDirectory.value / "app" / "assets" / "stylesheets" * "*.less"

templatesImport ++= Seq(
  "forms._",
  "io.sphere.client.model._",
  "io.sphere.client.shop.model._"
)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

javaSource in IntegrationTest := baseDirectory.value / "it"

resourceDirectory in IntegrationTest := baseDirectory.value / "it/resources"

lazy val root = (project in file(".")).configs(IntegrationTest).settings(Defaults.itSettings:_*)