package org.shkr.actors.play.encounter

import akka.actor.{Actor, ActorLogging}
import org.shkr.actors.play.encounter.Message.{NewConnection, RemoveConnection}

class Medium extends Actor with ActorLogging {

  def receive: Actor.Receive={

    case NewConnection(connection) => //DoSomething
    case RemoveConnection(connection) => //DoSomething
  }
}
