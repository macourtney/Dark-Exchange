(ns darkexchange.model.trust-score
  (:require [clj-record.boot :as clj-record-boot]
            [darkexchange.model.identity :as identity])
  (:use darkexchange.model.base))

(clj-record.core/init-model
  (:associations (belongs-to scorer :fk scorer_id :model identity)
                 (belongs-to target :fk target_id :model identity)))

(defn add-trust-score [target-identity basic-score]
  (insert { :scorer_id (:id (identity/current-user-identity)) :target_id (:id target-identity) :basic basic-score }))