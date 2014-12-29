(ns consens.server.core
  "Ring server running paxos"
  (:use [ring.middleware.params])
  (:require [ring.util.response :refer :all]))

(defn handler
  "Ring handler for paxos messages and client requests"
  [{:keys [request-method uri params] :as request}]
  (-> (response (str request-method " " uri))
      (content-type "text/plain")))

(def app
  (wrap-params handler))
