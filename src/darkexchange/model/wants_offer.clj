(ns darkexchange.model.wants-offer
  (:require [darkexchange.model.currency :as currency]
            [darkexchange.model.payment-type :as payment-type])
  (:use darkexchange.model.base))

(clj-record.core/init-model
  (:associations (belongs-to offer)))

(defn currency [wants-offer]
  (currency/get-currency (:currency wants-offer)))

(defn payment-type [wants-offer]
  (payment-type/get-payment (:payment_type wants-offer)))

(defn amount-str [wants-offer]
  (str (:amount wants-offer) " " (currency/currency-str (currency wants-offer))))

(defn currency-str [wants-offer]
  (payment-type/payment-type-str (payment-type wants-offer)))

(defn delete-wants-offers-for [offer-id]
  (doseq [wants-offer (find-records { :offer_id offer-id })]
    (destroy-record wants-offer)))