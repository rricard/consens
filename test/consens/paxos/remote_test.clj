(ns consens.paxos.remote-test
  (:require [clojure.test :refer :all]
            [consens.paxos.remote :refer :all]
            [org.httpkit.fake :refer :all]
            [clojure.data.json :as json]))

(deftest remote-test
  (let [base "http://remote/test"
        k (str (java.util.UUID/randomUUID))
        d (str (java.util.UUID/randomUUID))
        sn (rand-int 100)
        uri (str base "/" k)
        sns {"/key" 10}]
    (with-fake-http [{:url uri
                      :method :post
                      :headers {"X-SeqNum" (str sn)}}
                     {:status 202 :body "accepted"}
                     {:url uri
                      :method :put
                      :headers {"X-SeqNum" (str sn)}}
                     {:status 201 :body "created"}
                     {:url base
                      :method :get
                      :headers {"X-SeqNum" "Sync"}}
                     {:status 200 :body (json/write-str sns)}]
      (testing "consens.paxos.remote/prep"
        (is (= (prep base k sn d) "accepted")))
      (testing "consens.paxos.remote/accp"
        (is (= (accp base k sn) "created")))
      (testing "consens.paxos.remote/sn-get"
        (is (= (sn-get base) sns))))))
