(ns test.darkexchange.model.actions.action-init
  (:require [test.init :as test-init] ; Required to initialize the database.
            [darkexchange.model.server :as server-model]) 
  (:use darkexchange.model.actions.action-init
        clojure.test))

(deftest test-init
  (let [old-actions (server-model/action-map)]
    (server-model/reset-actions!)
    (init)
    (doseq [action-pair old-actions]
      (server-model/add-action (first action-pair) (second action-pair)))))