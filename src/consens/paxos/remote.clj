(ns consens.paxos.remote
  "Remote node proxy implementation"
  (:require [consens.remote :refer [proxcall url-join]]
            [org.httpkit.client :as http]))

(defn prep
  "Remote (paxos) preparation message to an another host on a key
  with a sequence number and data. A positive response is equivalent
  to a (paxos) promise from the remote."
  [host k sn d]
  (proxcall
    (http/post (url-join host k) {:headers {"X-SeqNum" (str sn)}
                                  :body d})
    202
    identity))

(defn accp
  "Remote (paxos) acceptation message to another host on a key
  with a sequence number. A positive response guarantees a consistent
  state."
  [host k sn]
  (proxcall
    (http/put (url-join host k) {:headers {"X-SeqNum" (str sn)}})
    201
    identity))
