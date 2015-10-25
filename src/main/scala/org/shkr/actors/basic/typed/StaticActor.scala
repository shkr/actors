package org.shkr.actors.basic.typed

import akka.typed.ScalaDSL._
import akka.typed.AskPattern._
import akka.typed._
import akka.util.Timeout
import scala.concurrent.Future
import scala.concurrent.duration._
/* using global pool since we want to run tasks after system shutdown */
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * An example definition of :
 * typed - The facility to define accepted message types of an Actor together with all reply types;
 * static actor - The behaviour of this actor is not capable of changing in response to a message,
 * it will stay the same until the Actor is stopped by its parent
 */
object StaticActor {

  object Configuration {
    val greeting: String = "Pranam"
    val systemName: String = "sanskaar"
  }

  /**
   * Message Envelopes used by this Actor
   */
  object Message {

    /* Greet Message */
    final case class Greet(whom: String, replyTo: ActorRef[Greeted])
    /* Response to Greet Message */
    final case class Greeted(whom: String)
  }

  /**
   * Definition of the actors
   */
  import Message._
  import Configuration._

  /**
   * This type [[Static]] is a variant of [[Total]] that does not
   * allow the actor to change behavior. The return type of the behaviour is Unit.
   * It is an efficient choice for stateless actors, possibly entering such a behavior after finishing its
   * initialization (which may be modeled using any of the other behavior types).
   * This behavior type is most useful for leaf actors that do not create child
   * actors themselves. All system signals are
   * ignored by this behavior, which implies that a failure of a child actor
   * will be escalated unconditionally.
   */
  val greeter: Static[Greet] = Static[Greet] { msg =>
    println(s"$greeting, ${msg.whom}!")
    msg.replyTo ! Greeted(msg.whom)
  }

  /**
   * An actor system is setup, then the driver thread sends a message to the StaticActor actor "sanskaar" decorated with Props
   * using the “ask” pattern (represented by the ? operator)
   * reference : http://doc.akka.io/docs/akka/snapshot/scala/typed.html
   */
  def main(args: Array[String]): Unit ={

    import Configuration._

    val system: ActorSystem[Greet] = ActorSystem(systemName, Props(greeter))
    implicit val timeout = Timeout(5 seconds)
    val future: Future[Greeted] = system ? (Greet("Dadaji", _))

    for {
      greeting <- future.recover { case ex => ex.getMessage }
      done <- { println(s"result: $greeting"); system.terminate() }
    } println("system terminated")
  }
}



