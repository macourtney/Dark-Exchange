(ns darkexchange.model.currency
  (:require [darkexchange.model.terms :as terms]))

(def currencies [ { :code "USD"     :name (terms/us-dollar) }
                  { :code "CAD"     :name (terms/canadian-dollar) }
                  { :code "EUR"     :name (terms/euro) }
                  { :code "GBP"     :name (terms/great-british-pound) }
                  { :code "AUD"     :name (terms/austrailian-dollar) }
                  { :code "CHF"     :name (terms/swiss-franc) }
                  { :code "BITCOIN" :name (terms/bitcoin) }])

(def currency-map (reduce #(assoc %1 (:code %2) %2) {} currencies ))

(defn get-currency [code]
  (get currency-map code))

(defn currency-str [currency]
  (:name currency))

; Simply allows the given currency to be displayed in a combobox or such.
(defrecord CurrencyDisplayAdaptor [currency]
  Object
  (toString [this] (currency-str (:currency this))))

(defn currency-adaptors []
  (map #(CurrencyDisplayAdaptor. %) currencies))