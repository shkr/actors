package org.shkr.actors.play.encounter

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.typesafe.config.ConfigFactory
import org.shkr.actors.play.encounter.Message._

class Participant extends Actor with ActorLogging {

  import context._

  val cluster = Cluster(context.system)

  var platforms = IndexedSeq.empty[ActorRef]

  val consoleReader = system.actorOf(Props(classOf[ConsoleReader], self), name = self.path.name + "_consolereader")
  val consoleWriter = system.actorOf(Props(classOf[ConsoleWriter], self), name = self.path.name + "_consolewriter")

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  def connection: Actor.Receive = {

    case ListenTo(connection) => //DoSomething
    case DropConnection(connection) => //DoSomething
  }

  def conversation: Actor.Receive={

    case Tell(msg, from, to) => platforms.headOption match {
      case Some(platform) => platform forward Tell(msg, from, to)
      case _ => log.info(s"Unable to tell messages. You are not logged in to any platform")
    }

    case Hear(msg, from, to) => {
      consoleWriter ! s"@$from says $msg \n"
    }
  }

  def network: Actor.Receive={

    case MemberUp(member) if member.hasRole("platform") => {
      log.info("Signing In to Platform available on member : {}", member.address)
      context.actorSelection(RootActorPath(member.address) / "user" / "platform_*") ! LogIn
    }

    case LoggedIn => {
      log.info("Logged In to Platform : {}", sender().path.name)
      context watch sender()
      platforms = platforms :+ sender()
      if(platforms.size==1){
        consoleReader ! StartConsole
        consoleWriter ! StartConsole
      }
    }
  }

  def receive: Actor.Receive = network orElse conversation orElse connection
}

object Participant {

  def main(args: Array[String]): Unit = {

    println(Console.BLUE_B + Console.WHITE + "usage with input: sbt" +
      " runMain 'org.shkr.actors.play.encounter.Participant <port[Int]> <username[String]>"
      + Console.RESET)

    val port: Int = args.headOption.getOrElse("7001").toInt

    val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [participant]")).
      withFallback(Configuration.config)

    // Create an Akka system
    val system = ActorSystem("EncounterSystem", config)

    system.actorOf(Props[Participant], name = args.drop(1).headOption.getOrElse("prakash"))
  }
}
