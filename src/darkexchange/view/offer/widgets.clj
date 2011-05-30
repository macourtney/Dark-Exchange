(ns darkexchange.view.offer.widgets
  (:require [seesaw.core :as seesaw-core]))

(defn create-text [id]
  (seesaw-core/text :id id :preferred-size [100 :by 25]))

(defn create-currency-combobox [id]
  (seesaw-core/combobox :id id :preferred-size [120 :by 25]))

(defn create-payment-type-combobox [id]
  (seesaw-core/combobox :id id :preferred-size [180 :by 25]))