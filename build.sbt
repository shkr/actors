name := "actors"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "krasserm at bintray" at "http://dl.bintray.com/krasserm/maven"
)

libraryDependencies ++= Seq(
  //Actors
  "com.typesafe.akka" %% "akka-actor" % "2.4.1",

  //Typed Actors
  "com.typesafe.akka" %% "akka-typed-experimental" % "2.4.1",

  //Cluster
  "com.typesafe.akka" %% "akka-cluster" % "2.4.1",

  //Persistence
  "com.typesafe.akka" %% "akka-persistence" % "2.4.1",
  "org.iq80.leveldb"  % "leveldb" % "0.7",
  "org.fusesource.leveldbjni" % "leveldbjni-all"   % "1.8",
  "com.github.krasserm" %% "akka-persistence-cassandra3" % "0.5"
)