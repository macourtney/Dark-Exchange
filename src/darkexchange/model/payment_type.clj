(ns darkexchange.model.payment-type
  (:require [darkexchange.model.currency :as currency]
            [darkexchange.model.terms :as terms]))

(def mailable-currencies ["USD" "AUD" "CAD" "CHF" "EUR" "GBP" ])

(def payment-types [{ :code "BITCOIN" :name (terms/bitcoin-transfer) :currency-types ["BITCOIN"] }
                    { :code "CAM" :name (terms/cash-by-mail) :currency-types mailable-currencies }
                    { :code "CHM" :name (terms/check-or-money-order-by-mail) :currency-types mailable-currencies }
                    { :code "DWOLLA" :name (terms/dwolla) :currency-types ["USD"] }])

(def payment-type-map (reduce #(assoc %1 (:code %2) %2) {} payment-types))

(defn payment-types-for-currency [currency-to-payment-type-map currency-code]
  (or (get currency-to-payment-type-map currency-code) #{}))

(defn add-payment-type-for-currency [currency-to-payment-type-map currency-code payment-type]
  (assoc currency-to-payment-type-map currency-code
    (conj (payment-types-for-currency currency-to-payment-type-map currency-code) payment-type)))

(defn add-payment-type-to-map [currency-to-payment-type-map payment-type]
  (reduce #(add-payment-type-for-currency %1 %2 payment-type) currency-to-payment-type-map
    (:currency-types payment-type)))

(defn create-currency-to-payment-type-map []
  (reduce #(add-payment-type-to-map %1 %2) {} payment-types))

(def currency-to-payment-type-map (create-currency-to-payment-type-map))

(defn get-payment [code]
  (get payment-type-map code))

(defn currencies-for [payment-type]
  (map currency/get-currency (:currency-types payment-type)))

(defn payment-type-str [payment-type]
  (:name payment-type))

(defn currency-and-payment-type-str [currency payment-type]
  (str (currency/currency-str currency) " by " (payment-type-str payment-type)))

(defn payment-types-for [currency-code]
  (get currency-to-payment-type-map currency-code))

; Simply allows the given currency to be displayed in a combobox or such.
(defrecord PaymentTypeDisplayAdaptor [payment-type]
  Object
  (toString [this] (payment-type-str (:payment-type this))))

(defn payment-type-adaptors []
  (map #(PaymentTypeDisplayAdaptor. %) payment-types))

(defn payment-type-adaptors-for [currency-code]
  (map #(PaymentTypeDisplayAdaptor. %) (payment-types-for currency-code)))