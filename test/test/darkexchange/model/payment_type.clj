(ns test.darkexchange.model.payment-type
  (:require [darkexchange.model.currency :as currency-model]) 
  (:use clojure.contrib.test-is
        darkexchange.model.payment-type))

(deftest test-currency-to-payment-type-map
  (is currency-to-payment-type-map "Currency to payment type map was not generated properly.")
  (is (= (sort (keys currency-to-payment-type-map)) (sort (keys currency-model/currency-map)))) 
  (is (= 1 (count (get currency-to-payment-type-map "BITCOIN"))) "The currency to payment type map has too many bitcoin payment types.")
  (is (= (get-payment "BITCOIN") (first (get currency-to-payment-type-map "BITCOIN"))) "The currency to payment type map has too many bitcoin payment types."))

(deftest test-payment-types-for
  (is (= #{(get-payment "BITCOIN")} (payment-types-for "BITCOIN")) "Invalid payment type associated with currency code \"BITCOIN\""))