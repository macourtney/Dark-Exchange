(ns darkexchange.controller.offer.wants-panel
  (:require [darkexchange.controller.offer.widgets :as offer-widgets]
            [darkexchange.model.currency :as currency-model]
            [darkexchange.model.payment-type :as payment-type-model]
            [seesaw.core :as seesaw-core]))

(defn find-i-want-amount [parent-component]
  (seesaw-core/select parent-component ["#i-want-amount"]))

(defn find-i-want-currency-combobox [parent-component]
  (seesaw-core/select parent-component ["#i-want-currency"]))

(defn find-i-want-payment-type-combobox [parent-component]
  (seesaw-core/select parent-component ["#i-want-payment-type"]))

(defn i-want-amount [parent-component]
  (Integer/parseInt (seesaw-core/text (find-i-want-amount parent-component))))

(defn i-want-currency [parent-component]
  (:currency (seesaw-core/selection (find-i-want-currency-combobox parent-component))))

(defn i-want-payment-type [parent-component]
  (:payment-type (seesaw-core/selection (find-i-want-payment-type-combobox parent-component))))

(defn wants-offer [parent-component]
  { :wants_amount (i-want-amount parent-component)
    :wants_currency (:code (i-want-currency parent-component))
    :wants_payment_type (:code (i-want-payment-type parent-component)) })

(defn load-currencies [parent-component]
  (offer-widgets/load-combobox (find-i-want-currency-combobox parent-component) (currency-model/currency-adaptors))
  parent-component)

(defn load-payment-types [parent-component]
  (offer-widgets/load-combobox (find-i-want-payment-type-combobox parent-component)
    (payment-type-model/payment-type-adaptors))
  parent-component)

(defn load-data [parent-component]
  (load-payment-types (load-currencies parent-component)))