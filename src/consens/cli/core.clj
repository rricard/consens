(ns consens.cli.core
  "Command Line Interface for launching a consens node
   or interrogating a running node (client)"
  (:require [consens.cli.server :as server]
            [consens.cli.client :as client]))

(defn help
  "Print the help"
  []
  (println
    "consens, a paxos-based, distributed data structure server"))

(defn -main
  "Given the arguments, branches to the server or client mode"
  [& args]
  (let [mode (first args)
        other (rest args)]
    (case mode
      "join" (server/join other)
      "read" (client/rd other)
      "write" (client/write other)
      "help" (help)
      (help))))
