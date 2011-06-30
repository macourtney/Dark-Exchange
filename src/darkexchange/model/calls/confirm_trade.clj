(ns darkexchange.model.calls.confirm-trade
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.trade :as trade-model]))

(defn confirm-trade [trade]
  (trade-model/confirm-trade trade))

(defn call [trade]
  (identity-model/send-message (trade-model/find-identity trade) action-keys/confirm-trade-action-key
      { :trade-id (confirm-trade trade) }))