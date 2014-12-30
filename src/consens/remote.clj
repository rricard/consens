(ns consens.remote
  "Access a remote consens node"
  (:require [org.httpkit.client :as http]
            [cemerick.url :refer (url)]))

(defmacro proxcall
  "Shortcut for querying a remote node and managing the results"
  [prom expcode retfn]
  `(let [{status# :status error# :error body# :body} (deref ~prom)]
     (if (or (not= status# ~expcode) error#)
       (throw (Exception. (str status# " " error#)))
       (~retfn body#))))

(defn url-join
  "Join urls and output a string url"
  [& args]
  (str (apply url args)))

(defn rd
  "Read a key in a remote node"
  [uri k]
  (proxcall
    (http/get (url-join uri k))
    200
    identity))

(defn wr
  "Write data in a key in a remote node"
  [uri k d]
  (proxcall
    (http/put (url-join uri k) {:form-params {:d d}})
    201
    identity))
