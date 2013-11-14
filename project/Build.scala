import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "sphere-donut"
    val appVersion      = "1.0-SNAPSHOT"
    val appDependencies = Seq(javaCore, javaJdbc)

    // Only compile .less files directly in the stylesheets directory
    def customLessEntryPoints(base: File): PathFinder = (
        (base / "app" / "assets" / "stylesheets" * "*.less")
    )

    lazy val main = play.Project(appName, appVersion, appDependencies).settings(
        Seq(
            lessEntryPoints <<= baseDirectory(customLessEntryPoints),
            libraryDependencies ++= Libs.appDependencies,
            libraryDependencies ++= Libs.testDependencies,
            templatesImport ++= Seq(
                "utils.ViewHelper._",
                "forms._",
                "io.sphere.client.model._",
                "io.sphere.client.shop.model._"
            )
        ):_*
    )

    object Libs {
        val appDependencies = Seq(
            "io.sphere"             %%  "sphere-play-sdk"   %   "0.42.0",
            "org.jsoup"             %   "jsoup"             %   "1.7.1"
        )
        val testDependencies = Seq(
            "org.mockito"   %   "mockito-all"       %       "1.9.5"     %   "test"
        )
    }
}