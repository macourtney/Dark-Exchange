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
  (let [basic-trust-score 0.25
        target-identity (identity-model/get-record 1)
        target-identity2 (identity-model/get-record 3)
        [trust-score-id trust-score-id2] (create-chain target-identity target-identity2 basic-trust-score)
        trust-score2 (when trust-score-id2 (get-record trust-score-id2))
        single-chain-score (calculate-single-chain trust-score2)]
    (is single-chain-score)
    (is (= single-chain-score 0.0625))
    (destroy-trust-scores trust-score-id trust-score-id2)))

(deftest test-calculate-combined-score
  (let [basic-trust-score 0.5
        target-identity (identity-model/get-record 1)
        target-identity2 (identity-model/get-record 3)
        [trust-score-id trust-score-id2] (create-chain target-identity target-identity2 basic-trust-score)
        basic-trust-score2 0.25
        target-identity3 (identity-model/get-record 4)
        [trust-score-id3 trust-score-id4] (create-chain target-identity3 target-identity2 basic-trust-score2)
        combined-score (calculate-combined-score target-identity2)]
    (is (= combined-score 0.3125))
    (destroy-trust-scores trust-score-id trust-score-id2 trust-score-id3 trust-score-id4)))

(deftest test-update-combined-score
  (let [basic-trust-score 0.5
        target-identity (identity-model/get-record 1)
        target-identity2 (identity-model/get-record 3)
        [trust-score-id trust-score-id2] (create-chain target-identity target-identity2 basic-trust-score)
        basic-trust-score2 0.25
        target-identity3 (identity-model/get-record 4)
        [trust-score-id3 trust-score-id4] (create-chain target-identity3 target-identity2 basic-trust-score2)
        combined-trust-score-id (update-combined-score target-identity2)
        combined-trust-score (when combined-trust-score-id (get-record combined-trust-score-id))]
    (is combined-trust-score)
    (is (= (:combined combined-trust-score) 0.3125))
    (destroy-trust-scores combined-trust-score-id trust-score-id trust-score-id2 trust-score-id3 trust-score-id4)))