package org.shkr.actors.play.diningphilosophers

import akka.actor.{Props, ActorSystem}
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

    system.actorOf(Props[Philosopher], "Plato")

    // Schedules a shutdown of the DiningPhilosophers
    // system after [[Configuration.playTime]]
    Thread.sleep(Configuration.playTime)
    system.terminate()
  }
}
