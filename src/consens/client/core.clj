(ns consens.client.core
  (:require [consens.remote :as remote]))

(defn rd
  "Read call on the cluster"
  [cluster k]
  (let [feedback (map #(try (remote/rd % k)
                            (catch Exception e nil))
                      cluster)
        freqs (frequencies feedback)
        filtered (filter #(> (/ (get freqs %) (count feedback)) 1/2)
                         freqs)]
    (if (> (count filtered) 0)
      (first filtered)
      (throw (Exception. "No Consensus")))))

(defn wr
  "Write call on the cluster"
  [cluster k d]
  (let [rnd (rand (count cluster))]
    (try
      (remote/wr (nth cluster rnd) k d)
      (catch Exception e (wr cluster k d)))))
