(ns consens.server.core
  "Ring server running paxos"
  (:require [clojure.string :as str]
            [consens.paxos.handler :as paxos]))

(def app
  (let [cluster (str/split (or (System/getenv "CLUSTER") "") #",")
        join?   (or (System/getenv "JOIN") false)]
    (paxos/app cluster join?)))
