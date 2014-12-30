(ns consens.remote
  "Implement the protocol to access remote nodes"
  (:require [consens.protocol :refer :all]
            [org.httpkit.client :as http]))

(defmacro proxcall
  "Shortcut for querying a remote node and managing the results"
  [prom expcode retfn]
  `(let [{:keys [status# error# body#]} (deref ~prom)]
     (if (or (not= status# ~expcode) error#)
       (throw (Exception. (str status# " " error#)))
       (~retfn body#))))

(deftype RemoteConsens
  [uri]
  IConsens
  (rd [k]
    (proxcall
      (http/get (str uri k))
      200
      #(%)))
  (wr [k d]
    (proxcall
      (http/put (str uri k) {:form-params {:d d}})
      204
      #(true))))
