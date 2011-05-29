(ns test.darkexchange.model.actions.get-peers
  (:require [test.fixtures.peer :as peer-fixture]
            [darkexchange.model.actions.action-keys :as action-keys] 
            [darkexchange.model.peer :as peer-model]) 
  (:use clojure.test
        darkexchange.model.actions.get-peers))

(use-fixtures :once peer-fixture/fixture)

(deftest test-action
  (let [response-map (action {})
        destinations (:data response-map)]
    (is destinations "There are no destinations in the system.")
    (is (= 1 (count destinations)) "Expected only one destination in the system.")
    (is (= (:destination (first peer-fixture/records)) (first destinations)) "Unexpected destination returned.")))

(deftest test-action-key
  (is (= action-key action-keys/get-peers-action-key)))