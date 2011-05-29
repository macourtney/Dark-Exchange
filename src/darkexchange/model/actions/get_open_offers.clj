(ns darkexchange.model.actions.get-open-offers
  (:require [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.offer :as offer-model]))

(def action-key action-keys/get-open-offers-action-key)

(defn action [request-map]
  { :data (offer-model/open-offers) })