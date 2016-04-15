import play.sbt.PlayImport

organization := "io.sphere.shop"

name := "sphere-donut"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.6"

lazy val jvmSdkVersion = "1.0.0-RC1"

libraryDependencies ++= Seq(
  javaCore,
  javaJdbc,
  javaWs,
  "com.commercetools.sdk.jvm.core" % "commercetools-models" % jvmSdkVersion withSources(),
  "com.commercetools.sdk.jvm.core" % "commercetools-java-client" % jvmSdkVersion withSources(),
  "com.commercetools.sdk.jvm.core" % "commercetools-convenience" % jvmSdkVersion withSources(),
  "com.commercetools.sdk.jvm.scala-add-ons" %% "commercetools-play-2_4-java-client" % jvmSdkVersion withSources(),
  "org.apache.httpcomponents" % "httpasyncclient" % "4.0.2",
  "com.novocode" % "junit-interface" % "0.11" % "test,it",
  "org.assertj" % "assertj-core" % "3.1.0" % "test,it",
  "org.mockito" % "mockito-core" % "1.8.5"  % "test, it",
   PlayImport.component("play-test") % "it"
)

dependencyOverrides ++= Set (
  "com.google.guava" % "guava" % "18.0",
  "commons-io" % "commons-io" % "2.4",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.0",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.6.0",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.0",
  "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % "2.6.0",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.6.0",
  "junit" % "junit" % "4.12"
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