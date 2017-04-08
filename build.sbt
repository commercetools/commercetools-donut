
name := "commercetools-donut"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

lazy val jvmSdkVersion = "1.14.1"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava, SbtWeb)
  .configs(IntegrationTest, TestCommon.PlayTest)
  .settings(TestCommon.defaultSettings)

libraryDependencies ++= Seq(
  javaWs,
  "com.commercetools.sdk.jvm.core" % "commercetools-models" % jvmSdkVersion withSources(),
  "com.commercetools.sdk.jvm.core" % "commercetools-java-client" % jvmSdkVersion,
  "com.commercetools.sdk.jvm.core" % "commercetools-convenience" % jvmSdkVersion withSources()
)

includeFilter in (Assets, LessKeys.less) := "*.less"
excludeFilter in (Assets, LessKeys.less) := "colors.less"  | "mixins.less"  | "order.less"  | "product.less"
LessKeys.compress in Assets := true

//TODO
// no automatic js minimize via sbt-coffescript plugin in 2.4. Removed .min in template temporary