(ns test.darkexchange.model.offer
  (:require [test.darkexchange.util :as test-util]
            [test.fixtures.offer :as offer-fixture] 
            [test.fixtures.util :as fixtures-util]) 
  (:use clojure.contrib.test-is
        darkexchange.model.offer))

(fixtures-util/use-fixture-maps :once offer-fixture/fixture-map)

(deftest test-search-offers
  (test-util/login) 
  (let [search-results (search-offers { :i-want-currency "BITCOIN" :i-want-payment-type "BITCOIN" :i-have-currency "USD"
                                        :i-have-payment-type "CAM" })]
    (is search-results "No results returned")
    (is (= 1 (count search-results)) "Expected one offer returned.")
    (is (= (dissoc (first offer-fixture/records) :created_at) (dissoc (first search-results) :created_at)) (str "Unexpected offer returned: " (first search-results))))
  (let [search-results (search-offers { :i-want-currency "USD" :i-want-payment-type "CAM" :i-have-currency "BITCOIN"
                                        :i-have-payment-type "BITCOIN" })]
    (is search-results "Nil results returned unexpectedly.")
    (is (= 0 (count search-results)) "Expected no offers returned."))
  (test-util/logout))

(deftest test-save-decimal
  (let [has-amount 1.1
        wants-amount 1.2
        offer { :has_amount has-amount
                :has_currency "USD"
                :has_payment_type "CAM"
                :wants_amount wants-amount
                :wants_currency "BITCOIN"
                :wants_payment_type "BITCOIN" }
        offer-id (create-new-offer offer)]
    (is offer-id)
    (let [saved-offer (get-record offer-id)]
      (is (= (:has_amount saved-offer) has-amount))
      (is (= (:wants_amount saved-offer) wants-amount))
      (destroy-record saved-offer))))