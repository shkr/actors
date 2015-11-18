package org.shkr.actors.play.encounter

import java.io.{FileWriter, File}
import akka.actor.{ActorRef, ActorLogging, Actor}
import org.shkr.actors.play.encounter.Message.{StopConsole, StartConsole}

class ConsoleWriter(user: ActorRef) extends Actor with ActorLogging {

  val fileToWrite = new File(s"encounter/${user.path.name}.log")

  def write(ln: String): Unit = {
    val tailWriter: FileWriter = new FileWriter(fileToWrite, true)
    tailWriter.write(ln)
    tailWriter.close()
  }

  def close(): Unit = fileToWrite.deleteOnExit()

  def receive: Actor.Receive = {
    case statement: String => write(statement)
    case StartConsole => if(!fileToWrite.exists()) fileToWrite.createNewFile()
    case StopConsole => close()
    case _ => //DoNothing
  }
}
