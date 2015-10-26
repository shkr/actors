package org.shkr.actors.play.diningphilosophers

import akka.actor.{ActorLogging, Actor}
import org.shkr.actors.play.diningphilosophers.Message.{Eat, Think}

class Philosopher extends Actor with ActorLogging {

  import context._

  def startEating(): Unit={
    system.scheduler.scheduleOnce(Configuration.eatingTime) { self ! Think };
    log.info("I am eating.")
    become(eating)
  }

  def startThinking(): Unit={
    system.scheduler.scheduleOnce(Configuration.thinkingTime) { self ! Eat };
    log.info("I am thinking.")
    become(thinking)
  }

  def thinking: Actor.Receive ={
    case Eat => startEating()
  }

  def eating: Actor.Receive ={
    case Think => startThinking()
  }

  system.scheduler.scheduleOnce(Configuration.thinkingTime) { self ! Eat };
  def receive: Actor.Receive = thinking
}
