(ns test.darkexchange.model.trust-score
  (:require [test.init :as test-init]
            [darkexchange.model.identity :as identity-model]
            [test.fixtures.identity :as identity-fixture]
            [test.fixtures.util :as fixtures-util]) 
  (:use clojure.contrib.test-is
        darkexchange.model.trust-score))

(fixtures-util/use-fixture-maps :once identity-fixture/fixture-map)

(deftest test-add-trust-score
  (let [basic-trust-score 0.5
        target-identity (identity-model/get-record 1)
        trust-score-id (add-trust-score target-identity basic-trust-score)
        trust-score (when trust-score-id (get-record trust-score-id))]
    (is trust-score-id)
    (is (= basic-trust-score (:basic trust-score)))
    (is (= (:id (identity-model/current-user-identity)) (:scorer_id trust-score)))
    (is (= (:id target-identity) (:target_id trust-score)))
    (destroy-record trust-score)))