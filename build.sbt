import play.sbt.PlayImport

organization := "io.sphere.shop"

name := "sphere-donut"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

lazy val jvmSdkVersion = "1.14.1"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava, SbtWeb)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings:_*)

libraryDependencies ++= Seq(
  javaWs,
  "com.commercetools.sdk.jvm.core" % "commercetools-models" % jvmSdkVersion withSources(),
  "com.commercetools.sdk.jvm.core" % "commercetools-java-client" % jvmSdkVersion,
  "com.commercetools.sdk.jvm.core" % "commercetools-convenience" % jvmSdkVersion withSources(),
  "org.assertj" % "assertj-core" % "3.1.0" % "test,it",
  "org.mockito" % "mockito-core" % "2.7.9"  % "test, it",
   PlayImport.component("play-test") % "it"
)

dependencyOverrides ++= Set (
  "junit" % "junit" % "4.12"
)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

javaSource in IntegrationTest := baseDirectory.value / "it"
scalaSource in IntegrationTest := baseDirectory.value / "it"
resourceDirectory in IntegrationTest := baseDirectory.value / "it/resources"

includeFilter in (Assets, LessKeys.less) := "*.less"
excludeFilter in (Assets, LessKeys.less) := "colors.less"  | "mixins.less"  | "order.less"  | "product.less"
LessKeys.compress in Assets := true

//TODO
// no automatic js minimize via sbt-coffescript plugin in 2.4. Removed .min in template temporary