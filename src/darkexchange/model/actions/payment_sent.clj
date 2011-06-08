(ns darkexchange.model.actions.payment-sent
  (:require [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.actions.util :as action-utils]
            [darkexchange.model.trade :as trade-model]))

(def action-key action-keys/payment-sent-action-key)

(defn payment-sent [foreign-trade-id trade-partner-identity]
  (trade-model/foreign-payment-sent foreign-trade-id trade-partner-identity)
  "Ok")

(defn action [request-map]
  { :data (payment-sent (:trade-id (:data request-map)) (action-utils/from-identity request-map)) })