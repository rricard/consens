(ns consens.paxos.handler
  "Ring server running paxos"
  (:require [ring.util.response :as res]
            [consens.paxos.remote :as remote]
            [consens.paxos.core :as core]
            [clojure.data.json :as json]))

(defn- gone-res [] (-> (res/response "gone") (res/status 410)))

(defn get-storage-handler
  "Ring handler for get storage requests"
  [storage]
  (-> (res/content-type "application/json")
      (res/response (json/write-str storage))))

(defn get-snbuf-handler
  "Ring handler for get snbuf requests"
  [snbuf]
  (-> (res/response
        (json/write-str
          (reduce #(assoc %1 %2 {:sn (:sn (get snbuf %2))})
                  {}
                  (keys snbuf))))
      (res/content-type "application/json")))

(defn rd-handler
  "Ring handler for reading a key"
  [storage {:keys [uri]}]
  (res/response (core/rd storage uri)))

(defn wr-handler
  "Ring handler for intending to write a key"
  [cluster storage snbuf {:keys [uri body]}]
  (try
    (do
      (res/response (core/wr cluster storage snbuf uri (slurp body)))
      (-> (res/response "created") (res/status 201)))
    (catch Exception e (do (prn e) (gone-res)))))

(defn accp-handler
  "Ring handler for accept messages."
  [storage snbuf {:keys [uri headers] :as req}]
  (let [sn (Long/parseLong (get headers "x-seqnum"))]
    (try
      (do
        (prn storage snbuf uri sn)
        (res/response (core/accp storage snbuf uri sn))
        (-> (res/response "created") (res/status 201)))
      (catch Exception e (do (prn e) (gone-res))))))

(defn prep-handler
  "Ring handler for prepare messages."
  [snbuf {:keys [uri body headers]}]
  (let [sn (Long/parseLong (get headers "x-seqnum"))]
    (try
      (do
        (res/response (core/prep snbuf uri (slurp body) sn))
        (-> (res/response "accepted") (res/status 202)))
      (catch Exception e (do (prn e) (gone-res))))))

(defn handler
  "Ring handler for paxos messages and client requests.
  It's just a router !"
  [cluster storage snbuf {:keys [request-method headers] :as request}]
  (let [sn (get headers "x-seqnum")]
    (case request-method
      :get (if sn
             (get-snbuf-handler snbuf)
             (if (get headers "X-All")
               (get-storage-handler storage)
               (rd-handler storage request)))
      :put (if sn
             (accp-handler storage snbuf request)
             (wr-handler cluster storage snbuf request))
      :post (prep-handler snbuf request))))

(defn app
  "Initialize and return an handler closure.
  Initializes by joining the cluster or by starting empty."
  [cluster join?]
  (let [storage (atom {})
        snbuf (atom {})]
    (do
      (if join?
        (do
          (swap! storage #(%2) (remote/get-storage (first cluster)))
          (swap! snbuf #(%2) (remote/get-snbuf (first cluster)))))
      (partial handler cluster storage snbuf))))
