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
 

### Static Typed Actors
---

  * An application which sends a message to the StaticActor actor "sanskaar" decorated with Props
    using the “ask” pattern (represented by the ? operator)
    reference : http://doc.akka.io/docs/akka/snapshot/scala/typed.html

```
> sbt "runMain org.shkr.actors.statictyped.StaticActorApp"
 
```

### Peer-to-Peer Akka Cluster with Remote Actors 
---
 * [INTRODUCTION] An akka Cluster provides a fault-tolerant decentralized peer-to-peer based cluster membership service 
    with no single point of failure or single point of bottleneck. It does this using gossip protocols 
    and an automatic failure detector.
  * An application which takes as input port number, then starts a Cluster Listener Remote Actor on that port
    and joins itself as member of the Cluster whose seed nodes are listed as :
    ``` seed-nodes = [
          "akka.tcp://ClusterSystem@127.0.0.1:2551",
          "akka.tcp://ClusterSystem@127.0.0.1:2552"] ```
    in the application.conf file;      
    reference : http://doc.akka.io/docs/akka/snapshot/scala/cluster-usage.html

```
> sbt "runMain org.shkr.actors.cluster.SimpleClusterApp 2551"  
 # This creates a ClusterListener Remote actor which registers itself as the first seed node of the Akka Cluster
> sbt "runMain org.shkr.actors.cluster.SimpleClusterApp 2552" 
 # This creates a ClusterListener Remote actor which registers itself as the second seed node of the Akka Cluster
 > sbt "runMain org.shkr.actors.cluster.SimpleClusterApp 2440" 
 # This creates a ClusterListener Remote actor which joins the cluster which currently have alive at least 
 # one member on the two seed nodes; 
```


## TODO
---
1. I will add descriptive comments in the application.conf file;
