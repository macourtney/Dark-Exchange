(ns darkexchange.controller.offer.widgets
  (:require [darkexchange.controller.utils :as controller-utils]
            [darkexchange.model.currency :as currency-model]
            [darkexchange.model.payment-type :as payment-type-model]
            [seesaw.core :as seesaw-core]))

(defn load-combobox [combobox data]
  (seesaw-core/config! combobox :model data))

(defn load-currencies [currency-combobox]
  (load-combobox currency-combobox (currency-model/currency-adaptors)))

(defn selected-currency [currency-combobox]
  (:currency (.getSelectedItem currency-combobox)))

(defn load-payment-type-combobox [payment-type-combobox currency-combobox]
  (load-combobox payment-type-combobox
    (payment-type-model/payment-type-adaptors-for (:code (selected-currency currency-combobox)))))

(defn currency-listener [payment-type-loader e]
  (payment-type-loader (seesaw-core/to-frame e)))

(defn attach-currency-listener [currency-combobox payment-type-loader]
  (controller-utils/attach-item-listener currency-combobox #(currency-listener payment-type-loader %)))