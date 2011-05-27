(ns darkexchange.controller.offer.new-offer
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.model.currency :as currency-model]
            [darkexchange.model.has-offer :as has-offer-model]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.model.payment-type :as payment-type]
            [darkexchange.model.wants-offer :as wants-offer-model]
            [darkexchange.view.offer.new-offer :as new-offer-view]
            [seesaw.core :as seesaw-core])
  (:import [java.util Date]))

(defn find-cancel-button [new-offer-view]
  (seesaw-core/select new-offer-view ["#cancel-button"]))

(defn attach-cancel-action [new-offer-view]
  (seesaw-core/listen (find-cancel-button new-offer-view)
    :action actions-utils/close-window))

(defn find-create-offer-button [new-offer-view]
  (seesaw-core/select new-offer-view ["#create-offer-button"]))

(defn find-i-have-amount [new-offer-view]
  (seesaw-core/select new-offer-view ["#i-have-amount"]))

(defn find-i-want-amount [new-offer-view]
  (seesaw-core/select new-offer-view ["#i-want-amount"]))

(defn find-i-have-currency-combobox [new-offer-view]
  (seesaw-core/select new-offer-view ["#i-have-currency"]))

(defn find-i-want-currency-combobox [new-offer-view]
  (seesaw-core/select new-offer-view ["#i-want-currency"]))

(defn find-i-have-payment-type-combobox [new-offer-view]
  (seesaw-core/select new-offer-view ["#i-have-payment-type"]))

(defn find-i-want-payment-type-combobox [new-offer-view]
  (seesaw-core/select new-offer-view ["#i-want-payment-type"]))

(defn i-have-amount [new-offer-view]
  (Integer/parseInt (.getText (find-i-have-amount new-offer-view))))

(defn i-have-currency [new-offer-view]
  (:currency (.getSelectedItem (find-i-have-currency-combobox new-offer-view))))

(defn i-have-payment-type [new-offer-view]
  (:payment-type (.getSelectedItem (find-i-have-payment-type-combobox new-offer-view))))

(defn has-offer [new-offer-view offer-id]
  { :amount (i-have-amount new-offer-view)
    :currency (:code (i-have-currency new-offer-view))
    :payment_type (:code (i-have-payment-type new-offer-view))
    :offer_id offer-id })

(defn i-want-amount [new-offer-view]
  (Integer/parseInt (.getText (find-i-want-amount new-offer-view))))

(defn i-want-currency [new-offer-view]
  (:currency (.getSelectedItem (find-i-want-currency-combobox new-offer-view))))

(defn i-want-payment-type [new-offer-view]
  (:payment-type (.getSelectedItem (find-i-want-payment-type-combobox new-offer-view))))

(defn wants-offer [new-offer-view offer-id]
  { :amount (i-want-amount new-offer-view)
    :currency (:code (i-want-currency new-offer-view))
    :payment_type (:code (i-want-payment-type new-offer-view))
    :offer_id offer-id })

(defn scrape-has-offer [new-offer-view offer-id]
  (has-offer-model/insert (has-offer new-offer-view offer-id)))

(defn scrape-wants-offer [new-offer-view offer-id]
  (wants-offer-model/insert (wants-offer new-offer-view offer-id)))

(defn scrape-offer [new-offer-view]
  (let [offer-id (offer-model/insert { :created_at (new Date) })]
    (scrape-has-offer new-offer-view offer-id)
    (scrape-wants-offer new-offer-view offer-id)
    offer-id))

(defn attach-create-offer-action [new-offer-view call-back]
  (seesaw-core/listen (find-create-offer-button new-offer-view)
    :action (fn [e] (call-back (scrape-offer new-offer-view)) (actions-utils/close-window e))))

(defn attach [new-offer-view call-back]
  (attach-cancel-action new-offer-view)
  (attach-create-offer-action new-offer-view call-back))

(defn load-combobox [combobox data]
  (seesaw-core/config! combobox :model data))

(defn load-currency-comboboxes [new-offer-view]
  (let [currency-adaptors (currency-model/currency-adaptors)]
    (load-combobox (find-i-have-currency-combobox new-offer-view) currency-adaptors)
    (load-combobox (find-i-want-currency-combobox new-offer-view) currency-adaptors)))

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
    (attach new-offer-view call-back)))