(ns test.fixtures.util
  (:use darkexchange.database.util)
  (:require [test.init :as test-init]))

(defn run-fixture [fixture-table-name records function]
  (try
    (apply insert-into fixture-table-name records)
    (function)
    (finally 
      (delete fixture-table-name ["true"]))))