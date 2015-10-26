package org.shkr.actors.play.diningphilosophers

import akka.actor.{ActorRef, Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import org.shkr.actors.util.sampler.Select

import scala.util.Random

object Stage {

  def main(args: Array[String]): Unit={

    val config = ConfigFactory.parseString(
      """
        actor {

            # FQCN of the ActorRefProvider to be used; the below is the built-in default,
            # another one is akka.remote.RemoteActorRefProvider in the akka-remote bundle.
            provider = "akka.actor.LocalActorRefProvider"
        }
      """.stripMargin)

    println(Console.BLUE_B + Console.WHITE + "usage with input: sbt" +
      " runMain 'org.shkr.actors.play.diningphilosophers.Stage <diningPhilsophers[Int]>," +
      " <totalChopsticks[Int]>'"
      + Console.RESET)

    val (diningPhilosophers, totalChopsticks) = args.length==2 match {
      case true => (args(0).toInt, args(1).toInt)
      case false => (Configuration.diningPhilosophers, Configuration.chopstickBoxSize)
    }

    val system: ActorSystem = ActorSystem("DiningPhilosophers", config)
    val chopsticksBox: ActorRef = system.actorOf(Props(classOf[ChopsticksBox], totalChopsticks), "SilverChopsticks")
    val philosophers: List[String] = Select.randomSelect(diningPhilosophers, Configuration.philosophers.toList).distinct

    for(philosopher <- philosophers){
      system.actorOf(Props(classOf[Philosopher], chopsticksBox), philosopher)
      Thread.sleep(Configuration.philosopherEntryIntervalTime)
    }

    // Schedules a shutdown of the DiningPhilosophers
    // system after [[Configuration.playTime]]
    Thread.sleep(Configuration.playTime)
    system.terminate()
  }
}
