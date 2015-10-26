package org.shkr.actors.basic.cluster.transformation

import akka.actor._
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.{Cluster, Member, MemberStatus}
import com.typesafe.config.ConfigFactory
import Message._
import org.shkr.actors.basic.cluster.Configuration

/**
 * Backend
 */
class Backend extends Actor with ActorLogging {

  val cluster: Cluster = Cluster(context.system)

  // subscribe to cluster changes, MemberUp
  // re-subscribe when restart
  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case TransformationJob(text) => sender() ! TransformationResult(sender().path.name, text.toUpperCase, self.path.name)
    case state: CurrentClusterState =>
      state.members.filter(_.status == MemberStatus.Up) foreach register
    case MemberUp(m) => register(m)
  }

  def register(member: Member): Unit =
    if (member.hasRole("frontend")) {
        log.info("Member is Up: {} with roles: {}", member.address, member.roles)
        context.actorSelection(RootActorPath(member.address) / "user" / "*") ! BackendRegistration
    }
}

object Backend {

  def main(args: Array[String]): Unit = {

    println(Console.BLUE_B + Console.WHITE + "usage with input: sbt" +
      " runMain 'org.shkr.actors.basic.cluster.transformation.Backend <port[Int]>," +
      " <totalActors[Int]>'"
      + Console.RESET)

    val port: Int = args.headOption.getOrElse("2553").toInt
    val totalActors: Int  = args.drop(1).headOption.getOrElse("1").toInt

    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
      withFallback(Configuration.config)

    val system = ActorSystem("ClusterSystem", config)

    Cluster(system) registerOnMemberUp {
      val backend: IndexedSeq[ActorRef] = Range(0, totalActors)
        .map(id => system.actorOf(Props[Backend], name = s"backend_$id"))
    }
  }
}
