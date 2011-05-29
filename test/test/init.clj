(ns test.init
  (:import [java.io File])
  (:require [darkexchange.core :as darkexchange-core]
            [drift.execute :as drift-execute]))

(def test-init? (atom false)) 

(defn
  init-tests []
  (when (compare-and-set! test-init? false true)
    (println "Initializing test database.")
    (darkexchange-core/set-mode "test")
    (darkexchange-core/database-init)))

(init-tests)