package org.shkr.actors.statictyped

import akka.typed.ScalaDSL._
import akka.typed._

/**
 * An example definition of :
 * typed - The facility to define accepted message types of an Actor together with all reply types;
 * static actor - The behaviour of this actor is not capable of changing in response to a message,
 * it will stay the same until the Actor is stopped by its parent
 */
object StaticActor {

  /**
   * Message Envelopes used by this Actor
   */
  object Message {

    /* Salutation */
    final case class Pranam(whom: String, replyTo: ActorRef[AashirwaadFrom])
    /* Response to Salutation */
    final case class AashirwaadFrom(whom: String)
  }

  /**
   * Definition of the actors
   */
  import Message._

  val greeter: Static[Pranam] = Static[Pranam] { msg ⇒
    println(s"Pranam, ${msg.whom}!")
    msg.replyTo ! AashirwaadFrom(msg.whom)
  }
}



