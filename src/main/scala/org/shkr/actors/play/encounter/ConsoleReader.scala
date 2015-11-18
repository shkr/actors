package org.shkr.actors.play.encounter

import java.io.{File, BufferedReader, InputStreamReader}
import akka.actor.{ActorRef, ActorLogging, Actor}
import org.shkr.actors.play.encounter.Message.{StopConsole, StartConsole, Tell}
import org.shkr.actors.play.encounter.StatementUtil._
import org.shkr.actors.util.io.Tail

class ConsoleReader(user: ActorRef) extends Actor with ActorLogging {

  private val fileToRead = new File(s"encounter/${user.path.name}.log")
  private val tailReader = new BufferedReader(new InputStreamReader(Tail.follow(fileToRead)))

  def read(): Unit = tailReader.readLine match {
    case statement: String => {
      forwardToUser(statement)
      read()
    }
    case null => //DoNothing
  }
  def close(): Unit = tailReader.close()


  def receive: Actor.Receive = {
    case StartConsole => {
      if(!fileToRead.exists()) fileToRead.createNewFile()
      read()
    }
    case StopConsole =>  close()
    case _ => //DoNothing
  }

  def forwardToUser(ln: String): Unit = ln match {
    case HearPattern(from, msg) => //DoNothing
    case TellPattern(to, msg) => user ! Tell(msg, user.path.name, to)
    case _ => //DoNothing
  }
}
