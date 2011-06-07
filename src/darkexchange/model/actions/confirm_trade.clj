(ns darkexchange.model.actions.confirm-trade
  (:require [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.actions.util :as action-utils]
            [darkexchange.model.trade :as trade-model]))

(def action-key action-keys/confirm-trade-action-key)

(defn confirm-trade [foreign-trade-id trade-partner-identity]
  (trade-model/confirm-trade foreign-trade-id trade-partner-identity)
  "Ok")

(defn action [request-map]
  { :data (confirm-trade (:trade-id (:data request-map)) (action-utils/from-identity request-map)) })