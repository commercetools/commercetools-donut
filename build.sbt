organization := "io.sphere.shop"

name := "sphere-donut"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaCore,
  javaJdbc,
  javaWs,
  "io.sphere.sdk.jvm" % "sphere-models" % "1.0.0-M16" withSources(),
  "io.sphere.sdk.jvm" % "sphere-java-client-apache-async" % "1.0.0-M16" withSources(),
  "org.apache.httpcomponents" % "httpasyncclient" % "4.0.2",
  "io.sphere.sdk.jvm" % "sphere-convenience" % "1.0.0-M16" withSources(),
  //"io.sphere.sdk.jvm" %% "sphere-play-2_4-java-client" % "1.0.0-M16" withSources(),
  "com.novocode" % "junit-interface" % "0.11" % "test,it",
  "org.assertj" % "assertj-core" % "3.1.0" % "test,it",
  "org.mockito" % "mockito-core" % "1.8.5"  % "test",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.0",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.6.0",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.0",
  "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % "2.6.0",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.6.0"
   //play.Project.component("play-test") % "it"
)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

javaSource in IntegrationTest := baseDirectory.value / "it"

resourceDirectory in IntegrationTest := baseDirectory.value / "it/resources"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

includeFilter in (Assets, LessKeys.less) := "*.less"
excludeFilter in (Assets, LessKeys.less) := "colors.less"  | "mixins.less"  | "order.less"  | "product.less"
LessKeys.compress in Assets := true

//TODO
// no automatic js minimize via sbt-coffescript plugin in 2.4. Removed .min in template temporary

lazy val root = (project in file(".")).configs(IntegrationTest).settings(Defaults.itSettings:_*).enablePlugins(PlayJava, SbtWeb)