(ns darkexchange.model.has-offer
  (:require [darkexchange.model.currency :as currency]
            [darkexchange.model.payment-type :as payment-type])
  (:use darkexchange.model.base))

(clj-record.core/init-model
  (:associations (belongs-to offer)))

(defn currency [has-offer]
  (currency/get-currency (:currency has-offer)))

(defn payment-type [has-offer]
  (payment-type/get-payment (:payment_type has-offer)))

(defn amount-str [has-offer]
  (str (:amount has-offer) " " (currency/currency-str (currency has-offer))))

(defn currency-str [has-offer]
  (payment-type/payment-type-str (payment-type has-offer)))