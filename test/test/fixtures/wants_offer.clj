(ns test.fixtures.wants-offer
  (:use darkexchange.database.util)
  (:require [test.init :as test-init]
            [test.fixtures.offer :as offer-fixture]))

(def records [
  { :id 1
    :amount 1
    :currency "USD"
    :payment_type "CAM"
    :offer_id 1 }])

(def fixture-table-name :wants-offers)

(def fixture-map { :table fixture-table-name :records records :required-fixtures [offer-fixture/fixture-map] })