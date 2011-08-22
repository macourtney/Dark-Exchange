(ns test.fixtures.trust-score
  (:use darkexchange.database.util)
  (:require [test.fixtures.identity :as identity-fixture]
            [test.fixtures.user :as user-fixture]
            [test.fixtures.util :as fixtures-util]))

(def records [
  { :id 1
    :scorer_id 2
    :target_id 1
    :basic 0.5
    :combined 0.0 }
  { :id 2
    :scorer_id 1
    :target_id 3
    :basic 0.5
    :combined 0.0 }
  { :id 3
    :scorer_id 2
    :target_id 4
    :basic 0.25
    :combined 0.0 }
  { :id 4
    :scorer_id 4
    :target_id 3
    :basic 0.25
    :combined 0.0 }])

(def fixture-table-name :trust_scores)

(def fixture-map { :table fixture-table-name
                   :records records
                   :required-fixtures [identity-fixture/fixture-map user-fixture/fixture-map] })