package org.shkr.actors.cluster

import akka.actor.{RootActorPath, Actor}
import akka.cluster.{Member, MemberStatus, Cluster}
import Messages._
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}

/**
 * TransformationBackend
 */
class TransformationBackend extends Actor {

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
    if (member.hasRole("frontend"))
      context.actorSelection(RootActorPath(member.address) / "user" / "frontend") !
        BackendRegistration
}
