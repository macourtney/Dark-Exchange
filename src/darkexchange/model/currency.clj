(ns darkexchange.model.currency
  (:require [darkexchange.model.terms :as terms]))

(def currencies [{ :code "USD" :name (terms/us-dollar) }
                  { :code "CAD" :name (terms/canadian-dollar) }
                  { :code "EUR" :name (terms/euro) }
                  { :code "GBP" :name (terms/great-british-pound) }
                  { :code "AUD" :name (terms/austrailian-dollar) }
                  { :code "CHF" :name (terms/swiss-franc) }])

(defn currencies-to-map []
  (reduce #(assoc %1 (:code %2) %2) {} currencies ))

(def currency-map (currencies-to-map))

(defn find-currency [code]
  (get currency-map code))

(defn currencies-for [payment-type]
  (map find-currency (:currency-types payment-type)))

(defn currency-str [currency]
  (:name currency))

; Simply allows the given currency to be displayed in a combobox or such.
(defrecord CurrencyDisplayAdaptor [currency]
  Object
  (toString [this] (currency-str (:currency this))))

(defn currency-adaptors []
  (map #(CurrencyDisplayAdaptor. %) currencies))