(ns consens.remote
  "Access a remote consens node"
  (:require [org.httpkit.client :as http]
            [cemerick.url :refer (url)]))

(defn proxcall
  "Shortcut for querying a remote node and managing the results"
  [prom expcode retfn]
  (let [{:keys [status error body]} (deref prom)]
    (if (or (not= status expcode) error)
      (throw (Exception. (str status " " error)))
      (retfn body))))

(defn url-join
  "Join urls and output a string url"
  [& args]
  (str (apply url args)))

(defn rd
  "Read a key in a remote node"
  [host k]
  (proxcall
    (http/get (url-join host k))
    200
    identity))

(defn wr
  "Write data in a key in a remote node"
  [host k d]
  (proxcall
    (http/put (url-join host k) {:form-params {:d d}})
    201
    identity))
