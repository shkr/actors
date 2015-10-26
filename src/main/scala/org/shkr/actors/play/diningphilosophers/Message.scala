package org.shkr.actors.play.diningphilosophers

/**
 * Message used by the dining philosopher play
 */
object Message {

  trait Activity
  case object Eat extends Activity
  case object Think extends Activity
  case object PickLeftChopstick extends Activity
  case object PickRightChopstick extends Activity
  case object PutChopstick extends Activity
  case object Chopstick extends Activity
  case object ChopstickNotAvailable extends Activity
}
