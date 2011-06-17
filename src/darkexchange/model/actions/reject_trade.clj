(ns darkexchange.model.actions.reject-trade
  (:require [darkexchange.interchange-map-util :as interchange-map-util]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.trade :as trade-model]))

(def action-key action-keys/reject-trade-action-key)

(defn reject-trade [foreign-trade-id trade-partner-identity]
  { :id (:id (trade-model/reject-trade foreign-trade-id trade-partner-identity)) })

(defn action [request-map]
  { :data (reject-trade (:trade-id (:data request-map)) (interchange-map-util/from-identity request-map)) })