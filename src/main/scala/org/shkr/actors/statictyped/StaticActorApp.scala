package org.shkr.actors.statictyped

import akka.typed.AskPattern._
import akka.typed._
import akka.util.Timeout
import scala.concurrent.Future
import scala.concurrent.duration._
/* using global pool since we want to run tasks after system shutdown */
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * An actor system is setup, then the driver thread sends a message to the StaticActor actor "sanskaar" decorated with Props
 * using the “ask” pattern (represented by the ? operator)
 * reference : http://doc.akka.io/docs/akka/snapshot/scala/typed.html
 */
object StaticActorApp {

  def main(args: Array[String]): Unit ={

    import StaticActor.Message._
    import StaticActor._

    val system: ActorSystem[Pranam] = ActorSystem("sanskaar", Props(greeter))
    implicit val timeout = Timeout(5 seconds)
    val future: Future[AashirwaadFrom] = system ? (Pranam("Dadaji", _))

    for {
      greeting <- future.recover { case ex => ex.getMessage }
      done <- { println(s"result: $greeting"); system.terminate() }
    } println("system terminated")
  }
}
