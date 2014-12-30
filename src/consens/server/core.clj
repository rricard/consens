(ns consens.server.core
  "Ring server running paxos"
  (:require [ring.util.response :refer :all]
            [ring.middleware.params :refer [wrap-params]]
            [consens.paxos.handler :refer [handler]]))

(def app
  (wrap-params handler))
