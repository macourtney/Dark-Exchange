(ns test.darkexchange.model.actions.get-open-offers
  (:require [darkexchange.model.actions.action-keys :as action-keys] 
            [darkexchange.model.peer :as peer-model]
            [test.darkexchange.util :as test-util]
            [test.fixtures.offer :as offer-fixture]
            [test.fixtures.util :as fixtures-util]) 
  (:use clojure.test
        darkexchange.model.actions.get-open-offers))

(fixtures-util/use-fixture-maps :once offer-fixture/fixture-map)

(deftest test-action
  (try
    (test-util/login) 
    (let [response-map (action {})
          open-offers (:data response-map)]
      (is open-offers "There are no offers in the system.")
      (is (= 1 (count open-offers)) "Expected only one offer in the system.")
      (is (= (:id (first offer-fixture/records)) (:id (first open-offers))) "Unexpected offer returned."))
    (finally
      (test-util/logout))))

(deftest test-action-key
  (is (= action-key action-keys/get-open-offers-action-key)))