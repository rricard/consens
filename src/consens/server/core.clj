(ns consens.server.core
  "Ring server running paxos"
  (:require [ring.util.response :refer :all]
            [ring.middleware.params :refer [wrap-params]]
            [consens.paxos.handler :as paxos]))

(def app
  (wrap-params (paxos/app)))
