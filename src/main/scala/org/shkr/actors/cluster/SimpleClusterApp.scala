package org.shkr.actors.cluster

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props
/**
  * An application which takes as input port number, then starts a Cluster Listener Remote Actor on that port
    and joins itself as member of the Cluster whose seed nodes are listed as :
    seed-nodes = [
          "akka.tcp://ClusterSystem@127.0.0.1:2551",
          "akka.tcp://ClusterSystem@127.0.0.1:2552"]
    in the application.conf file;
    reference : http://doc.akka.io/docs/akka/snapshot/scala/cluster-usage.html
 */
object SimpleClusterApp {

  def main(args: Array[String]): Unit = {
    if (args.isEmpty)
      startup(Seq("2551", "2552", "0"))
    else
      startup(args)
  }

  def startup(ports: Seq[String]): Unit = {
    ports foreach { port =>
      // Override the configuration of the port
      val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
        withFallback(ConfigFactory.load())

      // Create an Akka system
      val system = ActorSystem("ClusterSystem", config)
      // Create an actor that handles cluster domain events
      system.actorOf(Props[ClusterListener], name = "clusterActor")
    }
  }
}