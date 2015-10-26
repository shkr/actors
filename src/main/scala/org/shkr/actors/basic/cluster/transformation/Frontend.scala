package org.shkr.actors.basic.cluster.transformation

import java.util.concurrent.atomic.AtomicInteger

import akka.actor._
import akka.cluster.Cluster
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import Message._
import org.shkr.actors.basic.cluster.Configuration
import scala.concurrent.duration._

/**
 * Frontend
 */
class Frontend extends Actor {

  var backends = IndexedSeq.empty[ActorRef]

  var jobCounter = 0

  def receive = {
    case job: TransformationJob if backends.isEmpty =>
      sender() ! JobFailed("No Backend Actors available. Please try again later", job)

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
object Frontend {

  def main(args: Array[String]): Unit = {

    println(Console.BLUE_B + Console.WHITE + "usage with input: sbt" +
      " runMain 'org.shkr.actors.basic.cluster.transformation.Frontend <port[Int]>" +
      " <totalActors[Int]>'"
      + Console.RESET)

    val port: Int = args.headOption.getOrElse("2552").toInt
    val totalActors: Int  = args.drop(1).headOption.getOrElse("1").toInt

    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]")).
      withFallback(Configuration.config)

    val system = ActorSystem("ClusterSystem", config)

    Cluster(system) registerOnMemberUp {

      //Start Frontend Actors
      val frontendActors: IndexedSeq[ActorRef] = Range(0, totalActors)
        .map(id => system.actorOf(Props[Frontend], name = s"frontend_$id"))

      //Start a method which provides fronEnd Actor in RoundRobin
      var roundRobin: Iterator[ActorRef] = Iterator.empty
      def nextActor: ActorRef = if(roundRobin.hasNext) roundRobin.next() else {
        roundRobin = frontendActors.toIterator
        roundRobin.next()
      }

      val counter = new AtomicInteger
      import system.dispatcher
      system.scheduler.schedule(2.seconds, 2.seconds) {
        implicit val timeout = Timeout(5 seconds)
        (nextActor ? TransformationJob("Namaste-" + counter.incrementAndGet())) onSuccess {
          case result => println(result)
        }
      }
    }
  }
}