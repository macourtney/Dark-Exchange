(ns darkexchange.model.payment-type
  (:require [darkexchange.model.currency :as currency]
            [darkexchange.model.terms :as terms]))

(def payment-types [{ :code "CAM" :name (terms/cash-by-mail) :currency-types :all }
                    { :code "CHM" :name (terms/check-or-money-order-by-mail) :currency-types :all }
                    { :code "DWOLLA" :name (terms/dwolla) :currency-types ["USD"] }])

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