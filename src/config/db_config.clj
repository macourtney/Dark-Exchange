;; This file is used to configure the database and connection.

(ns config.db-config
  (:require [clojure.contrib.java-utils :as java-utils]
            [clojure.contrib.logging :as logging]
            [config.environment :as environment]
            [darkexchange.database.h2 :as h2]))

(defn dbname [environment]
  (cond
     ;; The name of the production database to use.
     (= environment :production) "dark_exchange_production"

     ;; The name of the development database to use.
     (= environment :development) "dark_exchange_development"

     ;; The name of the test database to use.
     (= environment :test) "dark_exchange_test"))

(defn
#^{:doc "Returns the database flavor which is used by Conjure to connect to the database."}
  create-flavor [environment]
  (logging/info (str "Environment: " environment))
  (h2/flavor

    ;; Calculates the database to use.
    (dbname environment)

    "data/db/"))
            
(defn
  load-config []
  (let [environment (environment/environment-name)
        flavor (create-flavor (keyword environment))]
    (if flavor
      flavor
      (throw (new RuntimeException (str "Unknown environment: " environment ". Please check your conjure.environment system property."))))))
      