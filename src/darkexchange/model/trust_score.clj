(ns darkexchange.model.trust-score
  (:require [clj-record.boot :as clj-record-boot]
            [darkexchange.model.identity :as identity])
  (:use darkexchange.model.base))

(clj-record.core/init-model
  (:associations (belongs-to scorer :fk scorer_id :model identity)
                 (belongs-to target :fk target_id :model identity)))

(defn find-trust-score
  ([target-identity] (find-trust-score (identity/current-user-identity) target-identity))
  ([scorer-identity target-identity]
    (find-record { :scorer_id (:id scorer-identity) :target_id (:id target-identity) })))

(defn add-trust-score
  ([target-identity basic-score] (add-trust-score (identity/current-user-identity) target-identity basic-score))
  ([scorer-identity target-identity basic-score]
    (if-let [trust-score-id (:id (find-trust-score scorer-identity target-identity))]
      (do
        (update { :id trust-score-id :basic basic-score })
        trust-score-id)
      (insert { :scorer_id (:id scorer-identity) :target_id (:id target-identity) :basic basic-score }))))

