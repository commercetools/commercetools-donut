// Comment this to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Play framework integration
addSbtPlugin("play" % "sbt-plugin" % "2.1.1")