(ns test.darkexchange.model.actions.get-peers
  (:require [test.darkexchange.util :as test-util] ; test-util must be required first.
            [darkexchange.model.actions.action-keys :as action-keys] 
            [darkexchange.model.peer :as peer-model]
            [test.fixtures.peer :as peer-fixture]
            [test.fixtures.util :as fixtures-util]) 
  (:use clojure.test
        darkexchange.model.actions.get-peers))

(fixtures-util/use-fixture-maps :once peer-fixture/fixture-map)

(deftest test-action
  (let [response-map (action {})
        destinations (:data response-map)]
    (is destinations "There are no destinations in the system.")
    (is (= (count peer-fixture/records) (count destinations)) (str "Expected only " (count peer-fixture/records) " destinations in the system."))
    (is (= (:destination (first peer-fixture/records)) (first destinations)) "Unexpected destination returned.")))

(deftest test-action-key
  (is (= action-key action-keys/get-peers-action-key)))