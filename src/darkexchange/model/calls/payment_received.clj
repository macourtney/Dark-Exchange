(ns darkexchange.model.calls.payment-received
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.trade :as trade-model]))

(defn payment-received [trade]
  (trade-model/payment-received trade))

(defn call [trade]
  (identity-model/send-message (trade-model/find-identity trade) action-keys/payment-received-action-key
    { :trade-id (:id (payment-received trade)) }))