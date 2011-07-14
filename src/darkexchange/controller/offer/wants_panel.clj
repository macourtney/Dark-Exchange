(ns darkexchange.controller.offer.wants-panel
  (:require [darkexchange.controller.widgets.currency-combobox :as currency-combobox]
            [darkexchange.controller.widgets.payment-type-combobox :as payment-type-combobox]
            [seesaw.core :as seesaw-core])
  (:import [java.math BigDecimal]))

(defn find-i-want-amount [parent-component]
  (seesaw-core/select parent-component ["#i-want-amount"]))

(defn find-i-want-currency-combobox [parent-component]
  (seesaw-core/select parent-component ["#i-want-currency"]))

(defn find-i-want-payment-type-combobox [parent-component]
  (seesaw-core/select parent-component ["#i-want-payment-type"]))

(defn find-wants-panel [parent-component]
  (seesaw-core/select parent-component ["#wants-panel"]))

(defn i-want-amount [parent-component]
  (BigDecimal. (seesaw-core/text (find-i-want-amount parent-component))))

(defn i-want-currency [parent-component]
  (:currency (seesaw-core/selection (find-i-want-currency-combobox parent-component))))

(defn i-want-payment-type [parent-component]
  (:payment-type (seesaw-core/selection (find-i-want-payment-type-combobox parent-component))))

(defn wants-offer [parent-component]
  { :wants_amount (i-want-amount parent-component)
    :wants_currency (:code (i-want-currency parent-component))
    :wants_payment_type (:code (i-want-payment-type parent-component)) })

(defn load-currencies [parent-component]
  (currency-combobox/load-data (find-i-want-currency-combobox parent-component))
  parent-component)

(defn load-payment-types [parent-component]
  (payment-type-combobox/load-data
    (find-i-want-payment-type-combobox parent-component)
    (find-i-want-currency-combobox parent-component))
  parent-component)

(defn load-data [parent-component]
  (load-payment-types (load-currencies parent-component)))

(defn attach [parent-component]
  (payment-type-combobox/attach
    (find-i-want-payment-type-combobox parent-component)
    (find-i-want-currency-combobox parent-component))
  parent-component)