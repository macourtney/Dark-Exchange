(ns test.darkexchange.model.trust-score
  (:require [clojure.contrib.logging :as logging]
            [test.init :as test-init]
            [darkexchange.model.identity :as identity-model]
            [test.darkexchange.util :as test-util]
            [test.fixtures.trust-score :as trust-score]
            [test.fixtures.util :as fixtures-util]) 
  (:use clojure.contrib.test-is
        darkexchange.model.trust-score))

(fixtures-util/use-fixture-maps :once trust-score/fixture-map)

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

(defn create-chain
  ([intermediate-identity target-identity basic-trust-score]
    (create-chain intermediate-identity target-identity basic-trust-score basic-trust-score))
  ([intermediate-identity target-identity basic-trust-score1 basic-trust-score2]
    [ (add-trust-score intermediate-identity basic-trust-score1)
      (add-trust-score intermediate-identity target-identity basic-trust-score2)]))

(defn destroy-trust-scores [& trust-score-ids]
  (doseq [trust-score-id trust-score-ids]
    (destroy-record { :id trust-score-id })))

(deftest test-calculate-single-chain
  (test-util/login)
  (let [trust-score (get-record 2)
        single-chain-score (calculate-single-chain trust-score)]
    (is single-chain-score)
    (is (= single-chain-score 0.25)))
  (test-util/logout))

(deftest test-calculate-combined-score
  (test-util/login)
  (let [combined-score (calculate-combined-score (identity-model/get-record 3))]
    (is (= combined-score 0.3125)))
  (test-util/logout))

(deftest test-update-combined-score
  (test-util/login)
  (let [combined-trust-score-id (update-combined-score (identity-model/get-record 3))
        combined-trust-score (when combined-trust-score-id (get-record combined-trust-score-id))]
    (is combined-trust-score)
    (is (= (:scorer_id combined-trust-score) (:id (identity-model/current-user-identity))))
    (is (= (:target_id combined-trust-score) 3))
    (is (= (:combined combined-trust-score) 0.3125))
    (is (= (:basic combined-trust-score) 0.0))
    (destroy-record combined-trust-score))
  (test-util/logout))

(deftest test-my-scores
  (test-util/login)
  (let [scores (my-scores)]
    (is scores)
    (is (= (count scores) 2)))
  (test-util/logout))