(ns darkexchange.model.actions.payment-received
  (:require [darkexchange.interchange-map-util :as interchange-map-util]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.trade :as trade-model]))

(def action-key action-keys/payment-received-action-key)

(defn payment-received [foreign-trade-id trade-partner-identity]
  (trade-model/foreign-payment-received foreign-trade-id trade-partner-identity)
  "Ok")

(defn action [request-map]
  { :data (payment-received (:trade-id (:data request-map)) (interchange-map-util/from-identity request-map)) })