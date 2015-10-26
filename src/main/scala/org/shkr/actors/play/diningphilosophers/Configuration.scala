package org.shkr.actors.play.diningphilosophers

import scala.concurrent.duration._

/**
 * Configuration object for the dining philosopher play
 */
object Configuration {

  val chopstickBoxSize: Int = 3
  val diningPhilosophers: Int = 5
  val philosophers: Set[String] = Set[String]("Plato", "Descartes", "Chanakya", "Kant", "Krishna", "Shiva",
                                              "Dostoevosky", "Liebniz", "Sridhara", "Amalananda")
  val philosopherEntryIntervalTime: Int = 2*1000 //In Milliseconds
  val eatingTime: FiniteDuration = 2.5.seconds
  val thinkingTime: FiniteDuration = 5.seconds
  val playTime: Long = 60*1000 //In Milliseconds
}
