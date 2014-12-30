(ns consens.remote-test
  (:require [clojure.test :refer :all]
            [consens.remote :refer :all]
            [org.httpkit.fake :refer :all]))

(deftest remote-test
  (let [base "http://remote/test"
        k (str (java.util.UUID/randomUUID))
        d (str (java.util.UUID/randomUUID))
        uri (str base "/" k)]
    (println uri)
    (with-fake-http [{:url uri :method :get} {:status 200 :body d}
                     {:url uri :method :put} {:status 201 :body "created"}]
      (testing "consens.remote/rd"
        (is (= (rd base k) d)))
      (testing "consens.remote/wr"
        (is (= (wr base k d) "created"))))))
