(defproject darkexchange "1.3.0-SNAPSHOT"
  :description "Dark Exchange is a distributed p2p exchange for bitcoin."
  :dependencies [[clojure-tools "1.0.0"]
                 [com.h2database/h2 "1.3.157"]
                 [commons-codec/commons-codec "1.5"]
                 [drift "1.2.2"]
                 [log4j/log4j "1.2.16"]
                 [org.clojars.macourtney/clj-record "1.0.2"]
                 [org.clojars.macourtney/i2p "0.8.7-0"]
                 [org.clojars.macourtney/mstreaming "0.8.7-0"]
                 [org.clojars.macourtney/streaming "0.8.7-0"]
                 [org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [seesaw "1.1.0"]]
  :dev-dependencies [[drift "1.2.2"]
                     [lein-tar "1.0.6"]]

  :main darkexchange.main

  :resources-path "pkg/resources"

  :run-aliases { :development darkexchange.development-main
                 :dev darkexchange.development-main } )
