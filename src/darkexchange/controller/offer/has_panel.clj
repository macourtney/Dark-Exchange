(ns darkexchange.controller.offer.has-panel
  (:require [darkexchange.controller.widgets.currency-combobox :as currency-combobox]
            [darkexchange.controller.widgets.payment-type-combobox :as payment-type-combobox]
            [seesaw.core :as seesaw-core])
  (:import [java.math BigDecimal]))

(defn find-i-have-amount [parent-component]
  (seesaw-core/select parent-component ["#i-have-amount"]))

(defn find-i-have-currency-combobox [parent-component]
  (seesaw-core/select parent-component ["#i-have-currency"]))

(defn find-i-have-payment-type-combobox [parent-component]
  (seesaw-core/select parent-component ["#i-have-payment-type"]))

(defn find-has-panel [parent-component]
  (seesaw-core/select parent-component ["#has-panel"]))

(defn i-have-amount [parent-component]
  (BigDecimal. (seesaw-core/text (find-i-have-amount parent-component))))

(defn i-have-currency [parent-component]
  (:currency (seesaw-core/selection (find-i-have-currency-combobox parent-component))))

(defn i-have-payment-type [parent-component]
  (:payment-type (seesaw-core/selection (find-i-have-payment-type-combobox parent-component))))

(defn has-offer [parent-component]
  { :has_amount (i-have-amount parent-component)
    :has_currency (:code (i-have-currency parent-component))
    :has_payment_type (:code (i-have-payment-type parent-component)) })

(defn load-currencies [parent-component]
  (currency-combobox/load-data (find-i-have-currency-combobox parent-component))
  parent-component)

(defn load-payment-types [parent-component]
  (payment-type-combobox/load-data
    (find-i-have-payment-type-combobox parent-component)
    (find-i-have-currency-combobox parent-component))
  parent-component)

(defn load-data [parent-component]
  (load-payment-types (load-currencies parent-component)))

(defn attach [parent-component]
  (payment-type-combobox/attach
    (find-i-have-payment-type-combobox parent-component)
    (find-i-have-currency-combobox parent-component))
  parent-component)