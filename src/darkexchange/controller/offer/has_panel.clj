(ns darkexchange.controller.offer.has-panel
  (:require [darkexchange.controller.offer.widgets :as offer-widgets]
            [darkexchange.model.currency :as currency-model]
            [darkexchange.model.payment-type :as payment-type-model]
            [seesaw.core :as seesaw-core]))

(defn find-i-have-amount [parent-component]
  (seesaw-core/select parent-component ["#i-have-amount"]))

(defn find-i-have-currency-combobox [parent-component]
  (seesaw-core/select parent-component ["#i-have-currency"]))

(defn find-i-have-payment-type-combobox [parent-component]
  (seesaw-core/select parent-component ["#i-have-payment-type"]))

(defn i-have-amount [parent-component]
  (Integer/parseInt (.getText (find-i-have-amount parent-component))))

(defn i-have-currency [parent-component]
  (:currency (.getSelectedItem (find-i-have-currency-combobox parent-component))))

(defn i-have-payment-type [parent-component]
  (:payment-type (.getSelectedItem (find-i-have-payment-type-combobox parent-component))))

(defn has-offer [parent-component offer-id]
  { :amount (i-have-amount parent-component)
    :currency (:code (i-have-currency parent-component))
    :payment_type (:code (i-have-payment-type parent-component)) })

(defn load-currencies [parent-component]
  (offer-widgets/load-combobox (find-i-have-currency-combobox parent-component) (currency-model/currency-adaptors)))

(defn load-payment-types [parent-component]
  (offer-widgets/load-combobox (find-i-have-payment-type-combobox parent-component)
    (payment-type-model/payment-type-adaptors)))

(defn load-data [parent-component]
  (load-currencies parent-component)
  (load-payment-types parent-component))