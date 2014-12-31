# consens

Consens is a library/server cluster for distributed consensus. It doesn't do much right now but we are already capable to boot up a paxos cluster.

## Installation

Download from https://github.com/rricard/consens.

## Usage

### Paxos server node

For now, everything is in development so everything will pass through leiningen.

Ok, so let's start a single node (which is pointless...)

    $ lein ring server

Let's store some keys inside and retrieve them ! (later this will be supported by a nice Clojure API too !)

    $ curl -XPUT localhost:3000/mykey -d mydata
    created
    $ curl -XGET localhost:3000/mykey
    mydata

At this point, nothing to be amazed !

Let's restart all of that by spinning 3 instances:

    # in tty1
    $ CLUSTER=http://localhost:4000,http://localhost:5000 lein ring server
    # in tty2
    $ CLUSTER=http://localhost:3000,http://localhost:5000 PORT=4000 lein ring server
    # in tty3
    $ CLUSTER=http://localhost:3000,http://localhost:4000 PORT=5000 lein ring server

Now let's write on one node and see it on the other nodes:

    $ curl -XPUT localhost:4000/mykey -d mydata
    created
    $ curl -XGET localhost:3000/mykey
    mydata
    $ curl -XGET localhost:4000/mykey
    mydata
    $ curl -XGET localhost:5000/mykey
    mydata

You can disconnect one node, write and still see a consensus. Disconnect the majority of the nodes, the consensus goes away !

If a node is disconnected, you can make him catch up with the cluster with JOIN:

    # tty3
    ^C
    $ CLUSTER=http://localhost:3000,http://localhost:4000 PORT=5000 JOIN=true lein ring server

JOIN will try to catch up with first node in the cluster list. JOIN will fail if the this node is unavailable too !

If all the nodes gets disconnected, well, you lose your data. So here's my advice for now: **DON'T YOU EVEN TRY TO USE THIS IN ANY KIND OF PRODUCTION SYSTEM**. Of course, I want to make it better but if you still want to put it in production for whatever crazy reason: **THIS IS MY FIRST CLOJURE PROJECT**.

But that doesn't keep you from playing with it and even helping me making it a real, usable system ! PRs are welcome !

### Roadmap

- **TEST THE HANDLERS**, yeah that's bad ! The core funcs are tested but the whole system is not. I don't know how to tackle the problem right now anyway, I have to think a little bit more about it...
- Simple, side-effect free, Client API. With futures if possible.
- Try to integrate it with existing clojure protocols, I'll need to read more literature on that before I think ...
- Oh ! and ... LOAD TESTING !

## License

Copyright © 2014-2015 Robin Ricard

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
