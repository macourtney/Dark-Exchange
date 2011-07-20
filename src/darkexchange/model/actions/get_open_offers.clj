(ns darkexchange.model.actions.get-open-offers
  (:require [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.offer :as offer-model]))

(def action-key action-keys/get-open-offers-action-key)

(defn get-open-offers []
  (map #(dissoc % :created_at) (offer-model/open-offers)))

(defn action [request-map]
  { :data (get-open-offers) })