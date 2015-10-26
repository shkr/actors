package org.shkr.actors.play.diningphilosophers

import akka.actor.{ActorRef, Props, ActorSystem}
import com.typesafe.config.ConfigFactory

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
    val system: ActorSystem = ActorSystem("DiningPhilosophers", config)
    val chopsticksBox: ActorRef = system.actorOf(Props[ChopsticksBox], "SilverChopsticks")
    val philosophers: Set[String] = Set[String]("Plato", "Descartes", "Chanakya", "Kant", "Krishna", "Shiva", "Dostoevosky")

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
