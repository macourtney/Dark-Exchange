(ns darkexchange.model.actions.payment-received
  (:require [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.actions.util :as action-utils]
            [darkexchange.model.trade :as trade-model]))

(def action-key action-keys/payment-received-action-key)

(defn payment-received [foreign-trade-id trade-partner-identity]
  (trade-model/foreign-payment-received foreign-trade-id trade-partner-identity)
  "Ok")

(defn action [request-map]
  { :data (payment-received (:trade-id (:data request-map)) (action-utils/from-identity request-map)) })