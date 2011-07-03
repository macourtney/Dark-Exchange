(ns darkexchange.model.actions.confirm-trade
  (:require [darkexchange.interchange-map-util :as interchange-map-util]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.trade :as trade-model]))

(def action-key action-keys/confirm-trade-action-key)

(defn confirm-trade [foreign-trade-id trade-partner-identity]
  { :id (trade-model/confirm-trade foreign-trade-id trade-partner-identity) })

(defn action [request-map]
  { :data (confirm-trade (:trade-id (:data request-map)) (interchange-map-util/from-identity request-map)) })