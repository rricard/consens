(defproject consens "0.1.0-SNAPSHOT"
  :description "Distributed clojure data structures through Paxos"
  :url "https://github.com/rricard/consens"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :main ^:skip-aot consens.cli.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
