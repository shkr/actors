name := "actors"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

unmanagedResources in Compile += file(".") / "conf" / "application.conf"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4-M3",
  "com.typesafe.akka" %% "akka-typed-experimental" % "2.4-M3",
  "com.typesafe.akka" %% "akka-cluster" % "2.4-M3",
  "com.typesafe.akka" %% "akka-cluster-metrics" % "2.4.0"
)