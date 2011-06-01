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
    :accept_confirm 0
    :has_amount 1
    :has_currency "BITCOIN"
    :has_payment_type "BITCOIN"
    :wants_amount 1
    :wants_currency "USD"
    :wants_payment_type "CAM" }])

(def fixture-table-name :offers)

(def fixture-map { :table fixture-table-name :records records :required-fixtures [user-fixture/fixture-map] })