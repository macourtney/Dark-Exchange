(ns darkexchange.controller.offer.new-offer
  (:require [darkexchange.controller.actions.window-actions :as window-actions]
            [darkexchange.model.currency :as currency-model]
            [darkexchange.model.payment-type :as payment-type]
            [darkexchange.view.offer.new-offer :as new-offer-view]
            [seesaw.core :as seesaw-core]))

(defn find-cancel-button [new-offer-view]
  (seesaw-core/select new-offer-view ["#cancel-button"]))

(defn attach-cancel-action [new-offer-view]
  (seesaw-core/listen (find-cancel-button new-offer-view)
    :action window-actions/close-window))

(defn attach [new-offer-view]
  (attach-cancel-action new-offer-view))

(defn load-combobox [combobox data]
  (seesaw-core/config! combobox :model data))

(defn find-i-have-currency-combobox [new-offer-view]
  (seesaw-core/select new-offer-view ["#i-have-currency"]))

(defn find-i-want-currency-combobox [new-offer-view]
  (seesaw-core/select new-offer-view ["#i-want-currency"]))

(defn load-currency-comboboxes [new-offer-view]
  (let [currency-adaptors (currency-model/currency-adaptors)]
    (load-combobox (find-i-have-currency-combobox new-offer-view) currency-adaptors)
    (load-combobox (find-i-want-currency-combobox new-offer-view) currency-adaptors)))

(defn find-i-have-payment-type-combobox [new-offer-view]
  (seesaw-core/select new-offer-view ["#i-have-payment-type"]))

(defn find-i-want-payment-type-combobox [new-offer-view]
  (seesaw-core/select new-offer-view ["#i-want-payment-type"]))

(defn load-payment-type-comboboxes [new-offer-view]
  (let [payment-type-adaptors (payment-type/payment-type-adaptors)]
    (load-combobox (find-i-have-payment-type-combobox new-offer-view) payment-type-adaptors)
    (load-combobox (find-i-want-payment-type-combobox new-offer-view) payment-type-adaptors)))

(defn load-data [new-offer-view]
  (load-currency-comboboxes new-offer-view)
  (load-payment-type-comboboxes new-offer-view))

(defn show [call-back]
  (let [new-offer-view (new-offer-view/show)]
    (load-data new-offer-view)
    (attach new-offer-view)))