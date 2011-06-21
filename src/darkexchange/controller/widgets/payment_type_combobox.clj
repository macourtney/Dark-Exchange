(ns darkexchange.controller.widgets.payment-type-combobox
  (:require [darkexchange.controller.widgets.currency-combobox :as currency-combobox]
            [darkexchange.controller.widgets.utils :as widgets-utils]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.model.payment-type :as payment-type-model]))

(defn valid-payment-type-adaptors [currency-combobox]
  (payment-type-model/payment-type-adaptors-for (currency-combobox/selected-currency-code currency-combobox)))

(defn load-payment-types [payment-type-combobox currency-combobox]
  (widgets-utils/load-combobox payment-type-combobox
    (valid-payment-type-adaptors currency-combobox)))

(defn load-data [payment-type-combobox currency-combobox]
  (load-payment-types payment-type-combobox currency-combobox))

(defn attach-currency-listener [payment-type-combobox currency-combobox]
  (controller-utils/attach-item-listener currency-combobox
    (fn [_] (load-payment-types payment-type-combobox currency-combobox))))

(defn attach [payment-type-combobox currency-combobox]
  (attach-currency-listener payment-type-combobox currency-combobox))