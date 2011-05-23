(ns darkexchange.model.has-offer
  (:require [darkexchange.model.currency :as currency-model])
  (:use darkexchange.model.base))

(clj-record.core/init-model
  (:associations (belongs-to offer)))

(defn currency [has-offer]
  (:currency has-offer))

(defn payment-type [has-offer]
  (:payment-type has-offer))

(defn currency-str [has-offer]
  (currency-model/currency-and-payment-type-str (currency has-offer) (payment-type has-offer)))