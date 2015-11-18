package org.shkr.actors.play.encounter

import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor._
import com.typesafe.config.ConfigFactory
import org.shkr.actors.play.encounter.Message._
/**
 * A Simple Platform for Instant Messaging
 * Backed by akka-cassandra
 * @todo Allow Participant to sign-in after being LoggedOut without Requiring to restart Platform
 */
class Platform extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  var participants = IndexedSeq.empty[ActorRef]

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    //#subscribe
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
    //#subscribe
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def conversation: Actor.Receive={

    case Tell(msg, from, to) => {
      val sendTo = participants.find(_.path.name.contentEquals(to))
      if(sendTo.isDefined){
        sendTo.get ! Hear(msg, from, to)
      } else {
        log.info(s"$to is not logged in to the platform(${self.path.name})")
      }
    }
  }

  def connection: Actor.Receive={

    case LogIn => {
      log.info("Logged In Participant : {}", sender().path.name)
      context watch sender()
      participants = participants :+ sender()
      sender() ! LoggedIn
    }
  }

  def network: Actor.Receive={

    case MemberUp(member) =>
      log.info("Member is Up: {} with roles: {}", member.address, member.roles)

    case UnreachableMember(member) if member.roles.contains("participant") =>
      val unreachableParticipants = participants.filter(_.path.address==member.address)
      participants = participants.filterNot(unreachableParticipants.contains)
      unreachableParticipants.foreach(p => {
        log.info("Logged Out Participant : {}", p.path.name)
        p ! LoggedOut
      })

  }

  def receive: Actor.Receive = network orElse connection orElse conversation
}

object Platform {

  def main(args: Array[String]): Unit = {

    println(Console.BLUE_B + Console.WHITE + "usage with input: sbt" +
      " runMain 'org.shkr.actors.play.encounter.Platform <port[Int]>"
      + Console.RESET)

    val port: Int = args.headOption.getOrElse("6060").toInt

    val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [platform]")).
      withFallback(Configuration.config)

    // Create an Akka system
    val system = ActorSystem("EncounterSystem", config)

    // Create an actor that listens to cluster domain events
    system.actorOf(Props[Platform], name = "platform_0")
  }
}

