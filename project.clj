(defproject darkexchange "0.1.0-SNAPSHOT"
  :description "Dark Exchange is a distributed p2p exchange for bitcoin."
  :dependencies [[bouncycastle/bcprov-jdk16 "136"]
                 [clojure-tools "1.0.0"]
                 [com.h2database/h2 "1.2.137"]
                 [commons-codec/commons-codec "1.5"]
                 [drift "1.2.1-SNAPSHOT"]
                 [log4j/log4j "1.2.16"]
                 [org.clojars.macourtney/clj-record "1.0.1"]
                 [org.clojars.macourtney/i2p "0.8.5-0"]
                 [org.clojars.macourtney/mstreaming "0.8.5-0"]
                 [org.clojars.macourtney/streaming "0.8.5-0"]
                 [org.clojure/clojure "1.2.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [seesaw "1.0.6"]]
  :dev-dependencies [[drift "1.2.0"]
                     [lein-tar "1.0.6"]]
  :main darkexchange.main

  :run-aliases { :development darkexchange.development-main }
  
  :manifest { "Name" "org/bouncycastle/asn1/ocsp/ResponderID.class"
              "SHA1-Digest" "STmNnCNaPJoaOnuwvVP6lO4LBLs=" })
