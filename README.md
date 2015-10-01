# Actors

A collection of actor-systems defined for different [akka projects](http://akka.io/docs/) by typesafe.
I have written these down to understand, play and build applications based on the akka projects; They 
may also serve as tutorial code for myself to refer in the future.

Always clean and compile the project before usage; It is good practise to glance through the conf/application.conf file 
so that you are familiar with how the application is configured.  

```
> sbt clean

> sbt compile
```

Note : this project uses the `akka.cluster.ClusterActorRefProvider` as the default actor provider.
 

### org.shkr.actors.statictyped : Static Typed Actors
---

  * An actor system (name = sanskaar) is setup, then the driver thread sends a message of type Pranam to the system 
     decorated with Props using the “ask” pattern (represented by the ? operator)
    reference : http://doc.akka.io/docs/akka/snapshot/scala/typed.html

```
> sbt "runMain org.shkr.actors.statictyped.StaticActorApp"
 
```

### org.shkr.actors.cluster.ClusterListener : Simple Peer-to-Peer Akka Cluster with Remote Actors 
---
 * `INTRODUCTION` An akka Cluster provides a fault-tolerant decentralized peer-to-peer based cluster membership service 
    with no single point of failure or single point of bottleneck. It does this using gossip protocols 
    and an automatic failure detector.
  * An actor system is linked to (name = "ClusterSystem") and the main method takes as input port number,
    then starts a Cluster Listener Remote Actor on that port
    and joins itself as member of the Cluster whose seed nodes are listed as :
    ``` seed-nodes = [
          "akka.tcp://ClusterSystem@127.0.0.1:2551",
          "akka.tcp://ClusterSystem@127.0.0.1:2552"] ```
    in the application.conf file;      
    reference : http://doc.akka.io/docs/akka/snapshot/scala/cluster-usage.html

```
> sbt "runMain org.shkr.actors.cluster.ClusterListener 2551"  
 # This creates a ClusterListener Remote actor which registers itself as the first seed node of the Akka Cluster
```

### org.shkr.actors.cluster.transformation : Back-end Worker Dial-In by Front-end Remote actor accepting requests Example 
---
  * An actor system is linked to (name = "ClusterSystem") and the main method takes as input port number,
    then starts a TransformationFrontEnd Listener Remote Actor on that port
    and joins itself as member of the Cluster whose seed nodes are listed as :
    ``` seed-nodes = [
          "akka.tcp://ClusterSystem@127.0.0.1:2551",
          "akka.tcp://ClusterSystem@127.0.0.1:2552"] ```
    in the application.conf file;      
    reference : http://doc.akka.io/docs/akka/snapshot/scala/cluster-usage.html

```
> sbt "runMain org.shkr.actors.cluster.transformation.TransformationFrontend 2552" 
 # This creates a TransformationFrontend Remote actor; The main method also schedules a TransformationJob
   for each 2 second interval which it sends to the TransformationFrontend Remote Actor
> sbt sbt "runMain org.shkr.actors.cluster.transformation.TransformationBackend 2553"  
 # This creates a TransformationBackend Remote actor which registers itself and listens to MemberUp events;
   In addition it informs the TransformationFrontend that it has registered itself, and does the
   any TransformationJob sent to it;
```


## TODO
---
1. I will add descriptive comments in the application.conf file;
