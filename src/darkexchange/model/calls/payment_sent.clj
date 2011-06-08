(ns darkexchange.model.calls.payment-sent
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.trade :as trade-model]))

(defn payment-sent [trade]
  (trade-model/payment-sent trade))

(defn call [trade]
  (identity-model/send-message (trade-model/find-identity trade) action-keys/payment-sent-action-key
    { :trade-id (:id (payment-sent trade)) }))