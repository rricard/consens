(defproject consens "0.1.0-SNAPSHOT"
  :description "Distributed clojure data structures through Paxos"
  :url "https://github.com/rricard/consens"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.3.2"]
                 [http-kit "2.1.16"] ]
  :plugins [[lein-ring "0.8.13"]]
  :main ^:skip-aot consens.cli.core
  :ring {:handler consens.server.core/app}
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
