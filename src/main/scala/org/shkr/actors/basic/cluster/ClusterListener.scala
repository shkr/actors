package org.shkr.actors.basic.cluster

import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor.{Props, ActorSystem, ActorLogging, Actor}
import com.typesafe.config.ConfigFactory
/**
 *
 * ClusterActor
 */
class ClusterListener extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    //#subscribe
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
    //#subscribe
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {} with roles: {}", member.address, member.roles)

    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignore
  }
}

object ClusterListener {

  def main(args: Array[String]): Unit = {

    println(Console.BLUE_B + Console.WHITE + "usage with input: sbt" +
      " runMain 'org.shkr.actors.basic.cluster.ClusterListener <port[Int]>"
      + Console.RESET)

    val port: Int = args.headOption.getOrElse("2551").toInt

    val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [listener]")).
      withFallback(Configuration.config)

    // Create an Akka system
    val system = ActorSystem("ClusterSystem", config)

    // Create an actor that listens to cluster domain events
    system.actorOf(Props[ClusterListener], name = "clusterActor")
  }
}
