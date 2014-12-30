(ns consens.server.core
  "Ring server running paxos"
  (:require [clojure.string :as str]
            [consens.paxos.handler :as paxos]))

(def app
  (let [cluster-str (System/getenv "CLUSTER")
        cluster (if cluster-str
                  (str/split cluster-str #",")
                  [])
        join?   (or (System/getenv "JOIN") false)]
    (paxos/app cluster join?)))
