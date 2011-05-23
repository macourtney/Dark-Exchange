(ns darkexchange.model.currency
  (:require [darkexchange.model.terms :as terms]))

(def currencies [{ :code "USD" :name (terms/us-dollar) }
                  { :code "CAD" :name (terms/canadian-dollar) }
                  { :code "EUR" :name (terms/euro) }
                  { :code "GBP" :name (terms/great-british-pound) }
                  { :code "AUD" :name (terms/austrailian-dollar) }
                  { :code "CHF" :name (terms/swiss-franc) }])

(def payment-types [{ :code "CAM" :name (terms/cash-by-mail) :currency-types :all }
                     { :code "CHM" :name (terms/check-or-money-order-by-mail) :currency-types :all }
                     { :code "DWOLLA" :name (terms/dwolla) :currency-types ["USD"] }])

(defn currencies-to-map []
  (reduce #(assoc %1 (:code %2) %2) {} currencies ))

(def currency-map (currencies-to-map))

(defn find-currency [code]
  (get currency-map code))

(defn currencies-for [payment-type]
  (map find-currency (:currency-types payment-type)))

(defn currency-str [currency]
  (:name currency))

(defn payment-type-str [payment-type]
  (:name payment-type))

(defn currency-and-payment-type-str [currency payment-type]
  (str (currency-str currency) " by " (payment-type-str payment-type)))