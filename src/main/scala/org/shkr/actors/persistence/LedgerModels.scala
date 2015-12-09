package org.shkr.actors.persistence

object LedgerModels {

  sealed trait Transaction { val owner: String; val amount: Double; val multiplier: Int}
  sealed trait Command

  case object SNAP extends Command
  case object PRINT extends Command

  case class Credit(owner: String, amount: Double, multiplier: Int) extends Transaction
  case class Debit(owner: String, amount: Double, multiplier: Int) extends Transaction

  case class Account(events: List[Transaction] = Nil) {

    def update(t: Transaction): Account = copy(t :: events)

    def balance: Double = events.map(t => t.amount*t.multiplier).sum

    override def toString: String =
      s"Account(\n Transaction : \n ${ events.map("# "+_).mkString("\n ")} \n \n \n Balance : $balance \n)"
  }
}
