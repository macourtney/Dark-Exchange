(ns darkexchange.model.calls.reject-trade
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.trade :as trade-model]))

(defn reject-trade [trade]
  (trade-model/reject-trade trade)
  (:id (trade-model/close trade)))

(defn call [trade]
  (identity-model/send-message (trade-model/find-identity trade) action-keys/reject-trade-action-key
      { :trade-id (reject-trade trade) }))