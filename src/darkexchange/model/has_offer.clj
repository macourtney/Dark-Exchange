(ns darkexchange.model.has-offer
  (:require [darkexchange.model.payment-type :as payment-type])
  (:use darkexchange.model.base))

(clj-record.core/init-model
  (:associations (belongs-to offer)))

(defn currency [has-offer]
  (:currency has-offer))

(defn payment-type [has-offer]
  (:payment-type has-offer))

(defn currency-str [has-offer]
  (payment-type/currency-and-payment-type-str (currency has-offer) (payment-type has-offer)))