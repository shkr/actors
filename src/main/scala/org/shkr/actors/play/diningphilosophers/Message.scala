package org.shkr.actors.play.diningphilosophers

/**
 * Message used by the dining philosopher play
 */
object Message {

  trait Activity
  case object Eat extends Activity
  case object Think extends Activity
}
