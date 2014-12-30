(ns consens.paxos.protocol
  "Paxos-specific protocol parts"
  (:require [consens.protocol :refer :all]))

(defprotocol IPaxos
  "Define a Paxos node"
  (prep [origin k sn d] "Receive a prepare message on a key
                        with a sequence number and the data
                        plus the originating node uri.")
  (prom [origin k sn] "Receive a promise from an another node")
  (accp [origin k sn] "Receive a an accept order"))
