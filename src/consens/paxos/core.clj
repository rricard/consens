(ns consens.paxos.core
  "Main node implementation"
  (:require [consens.paxos.remote :as remote]))

(defn rd
  "Read a key in a storage"
  [storage k]
  (get @storage k))

(defn- write-buf-and-inc
  [snbuf k d]
  (assoc snbuf k {:sn (inc (:sn (get snbuf k {:sn 0})))
                  :d d}))

(defn wr
  "Intend to write a key in a storage by using a seq number buffer and a
  cluster list"
  [cluster snbuf k d]
  (let [sn (do
             (swap! snbuf write-buf-and-inc k d)
             (:sn (get @snbuf k)))
        successes (map #(try
                          (do (remote/prep % k sn d) 1)
                          (catch Exception e 0))
                    cluster)]
    (if (> (/ (reduce + 0 successes) (count successes)) 1/2)
      (do (map #(try (remote/accp % k sn)) cluster) true)
      (throw (Exception. "Write Failed")))))
