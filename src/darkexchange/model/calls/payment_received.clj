(ns darkexchange.model.calls.payment-received
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.trade :as trade-model]))

(defn payment-received [trade]
  (trade-model/payment-received trade))

(defn update-trade [trade response]
  (when-let [data (:data response)]
    (trade-model/trade-updated trade)))

(defn call [trade]
  (update-trade trade
    (identity-model/send-message (trade-model/find-identity trade) action-keys/payment-received-action-key
      { :trade-id (:id (payment-received trade)) })))