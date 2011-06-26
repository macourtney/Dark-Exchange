(ns darkexchange.model.actions.accept-offer
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.model.trade :as trade-model]))

(def action-key action-keys/accept-offer-action-key)

(defn create-non-acceptor-trade [request-map offer]
  (let [user-map (:user (:from request-map))]
    (trade-model/create-non-acceptor-trade (:name user-map) (:public-key user-map)
      (:public-key-algorithm user-map) offer (:foreign-trade-id (:data request-map)))))

(defn reopen-offer [offer]
  (offer-model/reopen-offer offer)
  { :offer nil :trade-id nil })

(defn create-trade [request-map offer]
  (when offer
    (if-let [new-trade-id (create-non-acceptor-trade request-map offer)]
      { :offer (dissoc offer :created_at)
        :trade-id new-trade-id }
      (reopen-offer offer))))

(defn accept-offer [request-map]
  (create-trade request-map (offer-model/close-offer (:offer (:data request-map)))))

(defn action [request-map]
  { :data (accept-offer request-map) })