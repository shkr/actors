package org.shkr.actors.cluster.transformation

import akka.actor._
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.{Cluster, Member, MemberStatus}
import com.typesafe.config.ConfigFactory
import Messages._

/**
 * TransformationBackend
 */
class TransformationBackend extends Actor with ActorLogging {

  val cluster: Cluster = Cluster(context.system)

  // subscribe to cluster changes, MemberUp
  // re-subscribe when restart
  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case TransformationJob(text) => sender() ! TransformationResult(text.toUpperCase)
    case state: CurrentClusterState =>
      state.members.filter(_.status == MemberStatus.Up) foreach register
    case MemberUp(m) => register(m)
  }

  def register(member: Member): Unit =
    if (member.hasRole("frontend")) {
        log.info("Member is Up: {} with roles: {}", member.address, member.roles)
        context.actorSelection(RootActorPath(member.address) / "user" / "frontend") ! BackendRegistration
    }

}

//#backend
object TransformationBackend {

  def main(args: Array[String]): Unit = {
    // Override the configuration of the port when specified as program argument
    val port = if (args.isEmpty) "0" else args(0)
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
      withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[TransformationBackend], name = "backend")
  }
}
