(ns consens.paxos.handler
  "Ring server running paxos"
  (:require [ring.util.response :as res]
            [consens.paxos.remote :as remote]
            [clojure.string :as str]))

(defn handler
  "Ring handler for paxos messages and client requests"
  [cluster storage snbuf {:keys [request-method headers] :as request}]
  (-> (res/response (str request-method " " headers))
      (res/content-type "text/plain")))

(defn app
 "Initialize and return an handler closure"
 []
 (let [cluster (str/split (System/getenv "CLUSTER") ",")
                storage (atom {})
                snbuf (atom {})]
   (do
     (swap! storage #(%2) (remote/get-storage (first cluster)))
     (swap! snbuf #(%2) (remote/get-snbuf (first cluster)))
     (partial handler cluster storage snbuf))))
