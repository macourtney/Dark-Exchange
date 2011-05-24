(ns darkexchange.model.wants-offer
  (:require [darkexchange.model.payment-type :as payment-type])
  (:use darkexchange.model.base))

(clj-record.core/init-model
  (:associations (belongs-to offer)))

(defn currency [wants-offer]
  (:currency wants-offer))

(defn payment-type [wants-offer]
  (:payment-type wants-offer))

(defn currency-str [wants-offer]
  (payment-type/currency-and-payment-type-str (currency wants-offer) (payment-type wants-offer)))