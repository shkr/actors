package org.shkr.actors.basic.cluster

import com.typesafe.config.{Config, ConfigFactory}

/**
 * Copyright (c) 2015 Lumiata Inc.
 */
object Configuration {

  val config: Config = ConfigFactory.parseString("""
    # Set akka-cluster as the ActorRef Provider
    akka.actor.provider = "akka.cluster.ClusterActorRefProvider"

    # Disables logging of dead letters
    akka.log-dead-letters = off

    # Disable logging of lifecyle events
    akka.remote.log-remote-lifecycle-events = off

    # Set the hostname of the cluster for this actorSystem and its actors
    akka.remote.netty.tcp.hostname = "127.0.0.1"

    # Set initialization restrictions on the MemberUp for this node based on roles of existing actors in Cluster
    akka.cluster.role.frontend.min-nr-of-members = 1
    akka.cluster.role.backend.min-nr-of-members = 1

    # Set the seeds nodes for this cluster. This is critical for identification of the cluster
    akka.cluster.seed-nodes = [
      "akka.tcp://ClusterSystem@127.0.0.1:2551"
    ]

    # Set threshold for auto-down of unreachable node members
    akka.cluster.auto-down-unreachable-after = 2s

    # Disable legacy metrics in akka-cluster.
      akka.cluster.metrics.enabled=off

    # Enable metrics extension in akka-cluster-metrics.
      akka.extensions=["akka.cluster.metrics.ClusterMetricsExtension"]
  """)
}
