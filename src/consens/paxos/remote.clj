(ns consens.paxos.remote
  "Remote node proxy implementation"
  (:require [consens.paxos.protocol :refer :all]
            [consens.remote :refer :all]
            [org.httpkit.client :as http]))

(deftype RemotePaxos
  [uri]
  IPaxos
  (prep [origin k sn d]
    (proxcall
      (http/request :method :prepare
                    :url (str uri k)
                    :query-params {:sn sn
                                   :d d
                                   :origin origin})
      204
      #(true)))
  (prom [origin k sn]
    (proxcall
      (http/request :method :promise
                    :url (str uri k)
                    :query-params {:sn sn
                                   :origin origin})
      204
      #(true)))
  (accp [origin k sn]
    (proxcall
      (http/request :method :accept
                    :url (str uri k)
                    :query-params {:sn sn
                                   :origin origin})
      204
      #(true))))
