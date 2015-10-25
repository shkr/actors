package org.shkr.actors.basic.cluster.transformation

import java.util.concurrent.atomic.AtomicInteger

import akka.actor._
import akka.cluster.Cluster
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import Message._
import scala.concurrent.duration._

/**
 * TransformationFrontend
 */
class TransformationFrontend extends Actor {

  var backends = IndexedSeq.empty[ActorRef]

  var jobCounter = 0

  def receive = {
    case job: TransformationJob if backends.isEmpty =>
      sender() ! JobFailed("Service unavailable, try again later", job)

    case job: TransformationJob =>
      jobCounter += 1
      backends(jobCounter % backends.size) forward job

    case BackendRegistration if !backends.contains(sender()) =>
      context watch sender()
      backends = backends :+ sender()

    case Terminated(a) =>
      backends = backends.filterNot(_ == a)
  }
}

//#frontend
object TransformationFrontend {
  def main(args: Array[String]): Unit = {
    // Override the configuration of the port when specified as program argument
    val port = if (args.isEmpty) "0" else args(0)
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]")).
      withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)

    Cluster(system) registerOnMemberUp {
      val frontend = system.actorOf(Props[TransformationFrontend], name = "frontend")

      val counter = new AtomicInteger
      import system.dispatcher
      system.scheduler.schedule(2.seconds, 2.seconds) {
        implicit val timeout = Timeout(5 seconds)
        (frontend ? TransformationJob("Namaste-" + counter.incrementAndGet())) onSuccess {
          case result => println(result)
        }
      }
    }
  }
}