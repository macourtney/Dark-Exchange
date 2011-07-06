(ns test.init
  (:import [java.io File])
  (:require [darkexchange.core :as darkexchange-core]
            [drift.runner :as drift-runner]))

(def test-init? (atom false)) 

(defn
  init-tests []
  (when (compare-and-set! test-init? false true)
    (println "Initializing test database.")
    (darkexchange-core/set-mode "test")
    (darkexchange-core/environment-init)
    (drift-runner/update-to-version 0) ; Reset the test database
    (drift-runner/update-to-version Integer/MAX_VALUE)
    (darkexchange-core/database-init)))

(init-tests)