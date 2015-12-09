package org.shkr.actors.persistence

import akka.actor.{ActorSystem, Props}
import akka.persistence.{SnapshotOffer, PersistentActor}
import org.shkr.actors.persistence.LedgerModels._
import scala.collection.immutable.Seq
/**
  * A Ledger receives Credit or Debit commands
  * then it validates these commands, to produce transaction events which
  * are persisted and applied to the account state
  */
class Ledger(accountOwner: String) extends PersistentActor {

  val persistenceId: String = "Ledger-1"

  var state = Account()

  def updateState(t: Transaction): Unit ={
    state = state.update(t)
  }

  val receiveRecover: Receive = {
    case t: Transaction => updateState(t)
    case SnapshotOffer(_, account: Account) => state = account
  }

  val receiveCommand: Receive={

    case d: Debit => {
      if(d.owner==accountOwner){
        persistAllAsync(Seq[Debit](d)) { event =>
          updateState(event)
          context.system.eventStream.publish(event)
        }
      }
    }

    case c: Credit => {
      if(c.owner==accountOwner){
        persistAllAsync(Seq[Credit](c)) { event =>
          updateState(event)
          context.system.eventStream.publish(event)
        }
      }
    }

    case SNAP  => saveSnapshot(state)
    case PRINT => println(state)
  }
}

object Ledger {

  def props(accountOwner: String): Props = Props(classOf[Ledger], accountOwner)

  def main(args: Array[String]): Unit={

    println(Console.GREEN + "usage with input: sbt runMain 'org.shkr.actors.persistence.Ledger accounterOwner:String" + Console.RESET)

    val accountOwner: String = args.head
    val maxTransaction: Int = 10000
    val minTransaction: Int = 0

    // Create Configuration for ActorSystem
    val config = Configuration.config

    // Create an Akka system
    val system = ActorSystem("Persistence", config)

    // Create an actor that listens to cluster domain events
    val ledger = system.actorOf(props(accountOwner), name = "ledgerActor")

    ledger ! Debit(accountOwner,  ((maxTransaction-minTransaction) * scala.util.Random.nextDouble()).toInt, 1)
    ledger ! Credit(accountOwner, ((maxTransaction-minTransaction) * scala.util.Random.nextDouble()).toInt, -1)
    ledger ! SNAP
    ledger ! PRINT

    Thread.sleep(1000)
    system.terminate()
  }
}