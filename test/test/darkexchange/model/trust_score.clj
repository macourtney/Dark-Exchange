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
    (let [basic-trust-score2 -0.5
          trust-score-id2 (add-trust-score target-identity basic-trust-score2)
          trust-score2 (when trust-score-id2 (get-record trust-score-id2))]
      (is trust-score-id2)
      (is (= trust-score-id trust-score-id2))
      (is (= basic-trust-score2 (:basic trust-score2)))
      (when (not (= trust-score-id trust-score-id2))
        (destroy-record trust-score2)))
    (destroy-record trust-score))
  (let [basic-trust-score -0.5
        scorer-identity (identity-model/get-record 3)
        target-identity (identity-model/get-record 1)
        trust-score-id (add-trust-score scorer-identity target-identity basic-trust-score)
        trust-score (when trust-score-id (get-record trust-score-id))]
    (is trust-score-id)
    (is (= basic-trust-score (:basic trust-score)))
    (is (= (:id scorer-identity) (:scorer_id trust-score)))
    (is (= (:id target-identity) (:target_id trust-score)))
    (destroy-record trust-score)))