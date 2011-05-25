(ns darkexchange.model.payment-type
  (:require [darkexchange.model.currency :as currency]
            [darkexchange.model.terms :as terms]))

(def payment-types [{ :code "BITCOIN" :name (terms/bitcoin-transfer) :currency-types ["BITCOIN"] }
                    { :code "CAM" :name (terms/cash-by-mail) :currency-types :all }
                    { :code "CHM" :name (terms/check-or-money-order-by-mail) :currency-types :all }
                    { :code "DWOLLA" :name (terms/dwolla) :currency-types ["USD"] }])

(def payment-type-map (reduce #(assoc %1 (:code %2) %2) {} payment-types))

(defn get-payment [code]
  (get payment-type-map code))

(defn currencies-for [payment-type]
  (map currency/get-currency (:currency-types payment-type)))

(defn payment-type-str [payment-type]
  (:name payment-type))

(defn currency-and-payment-type-str [currency payment-type]
  (str (currency/currency-str currency) " by " (payment-type-str payment-type)))

; Simply allows the given currency to be displayed in a combobox or such.
(defrecord PaymentTypeDisplayAdaptor [payment-type]
  Object
  (toString [this] (payment-type-str (:payment-type this))))

(defn payment-type-adaptors []
  (map #(PaymentTypeDisplayAdaptor. %) payment-types))