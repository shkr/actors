package org.shkr.actors.play.encounter

object Message {

  sealed trait EncounterSignal

  case object LogIn extends EncounterSignal
  case object LoggedIn extends EncounterSignal
  case object LogOut extends EncounterSignal
  case object LoggedOut extends EncounterSignal

  case object StartConsole extends EncounterSignal
  case object StopConsole extends EncounterSignal

  case class Tell(msg: String, from: String, to: String) extends EncounterSignal
  case class Hear(msg: String, from: String, to: String) extends EncounterSignal

  sealed trait ConnectionActivity extends EncounterSignal

  case class ListenTo(connection: Connection) extends ConnectionActivity
  case class DropConnection(connection: Connection) extends ConnectionActivity

  case class Connection(name: String, var listeners: Set[String] = Set(), var transmitters: Set[String] = Set()) extends ConnectionActivity
  case class NewConnection(connection: Connection) extends ConnectionActivity
  case class RemoveConnection(connection: Connection) extends ConnectionActivity
}
