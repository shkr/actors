package org.shkr.actors.persistence

import com.typesafe.config.{ConfigFactory, Config}

object Configuration {

  val config: Config = ConfigFactory.parseString(
    """
    # Maximum Mailbox Capacity to prevent out-of-memory errors
    akka.actor.default-mailbox.stash-capacity=10000

    akka.actor.warn-about-java-serializer-usage = false

    ####################
    # Journal Plugin   #
    ####################

//    # Level DB and Local FileSystem persistence
//    # A new batch write is triggered by a persistent actor as soon as a batch reaches the maximum size
//    # or if the journal completed writing the previous batch
//    akka.persistence.journal.leveldb.max-message-batch-size = 200
//
//    akka.persistence.journal.plugin = "akka.persistence.journal.leveldb"
//    akka.persistence.snapshot-store.plugin = "akka.persistence.snapshot-store.local"
//
//    akka.persistence.journal.leveldb.dir = "target/ledger/journal"
//    akka.persistence.snapshot-store.local.dir = "target/ledger/snapshots"
//
//    # DO NOT USE THIS IN PRODUCTION !!!
//    # See also https://github.com/typesafehub/activator/issues/287
//    akka.persistence.journal.leveldb.native = false
//

    # Cassandra Persistence
    # activate the cassandra journal plugin
    akka.persistence.journal.plugin = "cassandra-journal"
    # activate the cassandra SNAPSHOT plugin
    akka.persistence.snapshot-store.plugin = "cassandra-snapshot-store"
    """)
}
