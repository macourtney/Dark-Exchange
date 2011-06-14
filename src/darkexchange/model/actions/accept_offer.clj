(ns darkexchange.model.actions.accept-offer
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.model.trade :as trade-model]))

(def action-key action-keys/accept-offer-action-key)

(defn create-non-acceptor-trade [request-data offer]
  (trade-model/create-non-acceptor-trade (:name request-data) (:public-key request-data)
    (:public-key-algorithm request-data) offer (:foreign-trade-id request-data)))

(defn create-trade [request-data offer]
  (when offer
    { :offer (dissoc offer :created_at)
      :trade-id (create-non-acceptor-trade request-data offer) }))

(defn accept-offer [request-data]
  (create-trade request-data (offer-model/close-offer (:offer request-data))))

(defn action [request-map]
  { :data (accept-offer (:data request-map)) })