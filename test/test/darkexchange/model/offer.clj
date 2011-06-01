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