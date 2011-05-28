(ns test.init
  (:import [java.io File])
  (:require [darkexchange.core :as darkexchange-core]
            [drift.execute :as drift-execute]))

(defn
  init-tests []
  (println "Initializing test database.")
  (darkexchange-core/set-mode "test")
  (darkexchange-core/database-init))