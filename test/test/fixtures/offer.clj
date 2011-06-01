(ns test.fixtures.offer
  (:use darkexchange.database.util)
  (:require [test.init :as test-init]
            [test.fixtures.user :as user-fixture]
            [test.fixtures.util :as fixtures-util])
  (:import [java.util Date]))

(def records [
  { :id 1
    :created_at (new Date)
    :identity_id nil
    :user_id 1
    :accept_confirm 0 }])

(def fixture-table-name :offers)

(def fixture-map { :table fixture-table-name :records records :required-fixtures [user-fixture/fixture-map] })