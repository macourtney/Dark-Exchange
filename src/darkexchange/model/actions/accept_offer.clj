(ns darkexchange.model.actions.accept-offer
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.model.trade :as trade-model]))

(def action-key action-keys/accept-offer-action-key)

(defn create-trade [offer]
  (when offer
    { :offer (dissoc offer :created_at)
      :trade-id (trade-model/create-new-trade { :offer_id (:id offer) :wants_first 1 }) }))

(defn accept-offer [request-data]
  (create-trade (offer-model/accept-offer (:name request-data) (:public-key request-data) (:offer request-data))))

(defn action [request-map]
  { :data (accept-offer (:data request-map)) })