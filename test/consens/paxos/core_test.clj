(ns consens.paxos.core-test
  (:require [clojure.test :refer :all]
            [consens.paxos.core :refer :all]
            [org.httpkit.fake :refer :all]))

(defn- gen-cluster
  [size]
  (map #(str "http://node-" %) (range size)))

(defn- gen-cluster-res
  [cluster consent k sn]
  (reduce #(conj %1 {:url (str %2 "/" k)
              :method :post
              :headers {"X-SeqNum" (str sn)}}
             (if consent
               {:status 202 :body "accepted"}
               {:status 409 :body "conflict"}))
          []
          cluster))

(deftest core-test
  (testing "consens.paxos.core/rd"
    (let [k (str (java.util.UUID/randomUUID))
          d (str (java.util.UUID/randomUUID))
          store (atom {k d})]
      (is (= (rd store k) d))))
  (testing "consens.paxos.core/wr"
    (let [cluster (gen-cluster 2)
          snbuf (atom {})
          k (str (java.util.UUID/randomUUID))]
      (testing "with a consenting cluster"
        (with-fake-http (gen-cluster-res cluster true k 1)
          (is (= (wr cluster snbuf k "d") true))))
      (testing "with a non-consenting cluster"
        (with-fake-http (gen-cluster-res cluster false k 2)
          (is (thrown? Exception (wr cluster snbuf k "d"))))))))
