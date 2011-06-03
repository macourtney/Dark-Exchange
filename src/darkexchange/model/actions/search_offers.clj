(ns darkexchange.model.actions.search-offers
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.offer :as offer-model]))

(def action-key action-keys/search-offers-action-key)

(defn scrub-offer [offer]
  (select-keys offer
    [:user :has_amount :has_currency :has_payment_type :wants_amount :wants_currency :wants_payment_type])) 

(defn action [request-map]
  { :data (map scrub-offer (offer-model/search-offers (:data request-map))) })