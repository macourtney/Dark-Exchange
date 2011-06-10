(ns darkexchange.model.actions.payment-sent
  (:require [darkexchange.interchange-map-util :as interchange-map-util]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.trade :as trade-model]))

(def action-key action-keys/payment-sent-action-key)

(defn payment-sent [foreign-trade-id trade-partner-identity]
  (trade-model/foreign-payment-sent foreign-trade-id trade-partner-identity)
  "Ok")

(defn action [request-map]
  { :data (payment-sent (:trade-id (:data request-map)) (interchange-map-util/from-identity request-map)) })