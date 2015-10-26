package org.shkr.actors.play.diningphilosophers

import akka.actor.{Actor, ActorLogging}
import org.shkr.actors.play.diningphilosophers.Message._

class ChopsticksBox(var totalChopsticks: Int) extends Actor with ActorLogging {
  
  def receive: Actor.Receive={
    case take @ (PickLeftChopstick | PickRightChopstick) => {
      log.info("Receive request for a chopstick.")
      if (totalChopsticks > 0) {
        totalChopsticks -= 1
        log.info("Fulfulling request for a chopstick.")
        sender() ! Chopstick
      } else {
        sender() ! ChopstickNotAvailable
      }
    }
    case PutChopstick => {
      totalChopsticks += 1
      log.info(s"Total Chopsticks in Box = $totalChopsticks")
    }
  }
}
