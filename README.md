# Actors

A collection of actor-systems defined on [akka](http://akka.io/docs/) by typesafe.

[API Documentation](http://shkr.github.io/actors)

#### PreStart
```
> sbt clean

> sbt compile
```

### Receive


#### actors.basic.typed : [Akka Typed](http://doc.akka.io/docs/akka/snapshot/scala/typed.html)
---

  * An actor system (name = sanskaar) is setup, then the driver thread sends a message of type Pranam to the system 
     decorated with Props using the “ask” pattern (represented by the ? operator)
```
> sbt "runMain org.shkr.actors.basic.typed.StaticActor"
 
```

#### basic.cluster.ClusterListener : [Akka Cluster](http://doc.akka.io/docs/akka/snapshot/common/cluster.html) w/ Remote Actors 
---
 * `INTRODUCTION` An akka Cluster provides a fault-tolerant decentralized peer-to-peer based cluster membership service 
    with no single point of failure or single point of bottleneck. It does this using gossip protocols 
    and an automatic failure detector.
  * An actor system is linked to (name = "ClusterSystem") and the main method takes as input port number,
    then starts a Cluster Listener Remote Actor on that port
    and joins itself as member of the Cluster whose seed nodes are listed as :
    ``` seed-nodes = [
          "akka.tcp://ClusterSystem@127.0.0.1:2551"
          ] ```
    in the [reference.conf](https://github.com/shkr/actors/tree/master/src/main/scala/org/shkr/actors/basic/cluster/conf/reference.conf) file;      
```
> sbt "runMain org.shkr.actors.basic.cluster.ClusterListener 2551"  
 # This creates a ClusterListener Remote actor which registers itself as the first seed node of the Akka Cluster
```
##### cluster.transformation.{Frontend, Backend} : Modified [Worker Dial-in Example](http://doc.akka.io/docs/akka/snapshot/scala/cluster-usage.html#Worker_Dial-in_Example) 
---
```
> sbt "runMain org.shkr.actors.basic.cluster.transformation.Frontend 2552 1" 
 # This creates a Frontend Remote actor; The main method also schedules a TransformationJob
   The ActorSystem on this node for each 2 second interval sends a Job to the Frontend Actors on this node
   in RoundRobin
> sbt "runMain org.shkr.actors.basic.cluster.transformation.Backend 2553 2"  
 # This creates a two Backend Remote actor which registers itself and listens to MemberUp events;
   In addition it informs the TransformationFrontend that it has registered itself, and does the
   any TransformationJob sent to it;
```

#### play.diningphilosophers : A Play of the [Dining Philisophers](https://en.wikipedia.org/wiki/Dining_philosophers_problem)
---
  * An actor system is linked to (name = "DiningPhilisophers") and using a LocalActorRefProvider,
    we enact the Dining Philosophers.
  * The actor system is configured inside the [Configuration](https://github.com/shkr/actors/blob/master/src/main/scala/org/shkr/actors/play/diningphilosophers/Configuration.scala)
    object which specifies the thinkingTime, eatingTime and the playTime

```
> sbt "runMain org.shkr.actors.play.diningphilosophers.Stage"
> sbt "runMain org.shkr.actors.play.diningphilosophers.Stage 8 4"
```

#### actors.persistence : Akka Persistence
---
  * Ledger Example
   * An actor system is created, and a Ledger Actor is intantiated to which some random transactions is sent. At the end 
    of which we save a snapshot and print the current-time state of the Ledger
   * The persistence plugin used is the LevelDB and Local FileSystem which is available as part of akka-persistence.
     Additionally these two imports will have to be added to the build file :
      - "org.iq80.leveldb"  % "leveldb" % "0.7",
      - "org.fusesource.leveldbjni" % "leveldbjni-all"   % "1.8"
   * The actor system is configured inside the [Configuration](https://github.com/shkr/actors/blob/master/src/main/scala/org/shkr/actors/persistence/Configuration.scala)

```
> sbt "runMain org.shkr.actors.persistence.Ledger Sonia"
```
