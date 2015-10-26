package org.shkr.actors.play.diningphilosophers

import scala.concurrent.duration._

/**
 * Configuration object for the dining philosopher play
 */
object Configuration {

  val eatingTime: FiniteDuration = 2.seconds
  val thinkingTime: FiniteDuration = 5.seconds
  val playTime: Long = 30*1000 //In Milliseconds
}
