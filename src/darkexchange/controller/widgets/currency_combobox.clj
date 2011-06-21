(ns darkexchange.controller.widgets.currency-combobox
  (:require [darkexchange.controller.widgets.utils :as widgets-utils]
            [darkexchange.model.currency :as currency-model]))

(defn selected-currency [currency-combobox]
  (:currency (.getSelectedItem currency-combobox)))

(defn selected-currency-code [currency-combobox]
  (:code (selected-currency currency-combobox)))

(defn load-currencies [currency-combobox]
  (widgets-utils/load-combobox currency-combobox (currency-model/currency-adaptors)))

(defn load-data [currency-combobox]
  (load-currencies currency-combobox))

(defn attach [currency-combobox]
  )