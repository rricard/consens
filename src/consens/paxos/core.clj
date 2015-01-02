(ns consens.paxos.core
  "Main node implementation"
  (:require [consens.paxos.remote :as remote]))

(defn rd
  "Read a key in a storage"
  [storage k]
  (get @storage k))

(defn- write-snbuf-and-inc
  [snbuf k d]
  (assoc snbuf k {:sn (inc (:sn (get snbuf k {:sn 0})))
                  :d d}))

(defn- write-snbuf-cond
  [snbuf k d prep-sn]
  (if (> prep-sn (:sn (get snbuf k {:sn 0})))
    (assoc snbuf k {:sn prep-sn
                    :d d})
    snbuf))

(defn prep
  "Receive a prepare message. Define if it should be accepted (promise) or rejected
  with the seq number buffer and the message seq num."
  [snbuf k d prep-sn]
  (let [before (get @snbuf k)]
    (do
      (swap! snbuf write-snbuf-cond k d prep-sn)
      (if (not= before (get @snbuf k))
        true
        (throw (Exception. "Outdated Prepare"))))))

(defn accp
  "Receive an accept message. Do the storage save if a new value has not been
  swapped already."
  [storage snbuf k sn]
  (let [savebuf (get @snbuf k)]
    (if (= sn (:sn savebuf))
      (do
        (swap! storage #(assoc % k (:d savebuf)))
        true)
      (throw (Exception. "Outdated Accept")))))

(defn wr
  "Intend to write a key in a storage by using a seq number buffer and a
  cluster list"
  [cluster storage snbuf k d]
  (let [sn (do
             (swap! snbuf write-snbuf-and-inc k d)
             (:sn (get @snbuf k)))
        feedback (conj (map #(try
                               (do (remote/prep % k sn d) 1)
                               (catch Exception e 0))
                            cluster)
                       1)]
    (if (> (/ (reduce + 0 feedback) (count feedback)) 1/2)
      (let [accepted (map #(try
                             (remote/accp % k sn)
                             (catch Exception e 0))
                          cluster)]
        (dorun accepted) ; needed to evaluate the remote calls
        (accp storage snbuf k sn))
      (throw (Exception. "Write Failed")))))
