package org.shkr.actors.play.diningphilosophers

import akka.actor.{ActorLogging, Actor}
import akka.actor._
import org.shkr.actors.play.diningphilosophers.Message._

class Philosopher(chopsticksBox: ActorRef) extends Actor with ActorLogging {

  import context._

  def startEating(): Unit={
    system.scheduler.scheduleOnce(Configuration.eatingTime) { self ! Think }
    log.info(Console.BLUE + Console.YELLOW_B + "I am eating." + Console.RESET)
    become(eating)
  }

  def startThinking(): Unit={
    system.scheduler.scheduleOnce(Configuration.thinkingTime) { self ! Eat }
    log.info(Console.GREEN + Console.YELLOW_B + "I am thinking." + Console.RESET)
    become(thinking)
  }

  def chopstickWaiting: Actor.Receive={
    case Chopstick => {
      log.info("I have two chopsticks. I will start eating.")
      startEating()
    }
    case ChopstickNotAvailable => {
      log.info("I cannot eat with one. I put my chopstick and start thinking.")
      chopsticksBox ! PutChopstick
      startThinking()
    }
  }

  def thinking: Actor.Receive ={
    case Eat =>
      log.info("I want to Eat.")
      log.info("I want two Chopsticks.")
      chopsticksBox ! PickLeftChopstick
      chopsticksBox ! PickRightChopstick

    case Chopstick => {
      log.info("I received one chopstick.")
      become(chopstickWaiting)
    }

    case ChopstickNotAvailable => {
      log.info("I was denied a chopstick.")
      become(deniedPhilosopher)
    }
  }

  def deniedPhilosopher: Actor.Receive={
    case Chopstick => {
      log.info("I cannot eat with one. I put my chopstick and start thinking.")
      chopsticksBox ! PutChopstick
      startThinking()
    }
    case ChopstickNotAvailable => {
      log.info("I do not have even one chopstick. I will start thinking.")
      startThinking()
    }
  }

  def takeChopsticks(): Unit={
    chopsticksBox ! PickLeftChopstick
    chopsticksBox ! PickRightChopstick
  }

  def eating: Actor.Receive ={
    case Think => {
      log.info("I want to think.")
      chopsticksBox ! PutChopstick
      chopsticksBox ! PutChopstick
      startThinking()
    }
  }

  system.scheduler.scheduleOnce(Configuration.thinkingTime) { self ! Eat };

  def receive: Actor.Receive = thinking
}
