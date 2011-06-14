(ns darkexchange.model.calls.accept-offer
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.interchange-map-util :as interchange-map-util]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.model.trade :as trade-model]
            [darkexchange.model.user :as user-model]))

(defn update-offer [initial-trade foreign-offer]
  (when foreign-offer
    (offer-model/update-from-foreign-offer (:offer_id initial-trade) foreign-offer)))

(defn find-offer [initial-trade response-map]
  (update-offer initial-trade (:offer (:data response-map))))

(defn update-trade [initial-trade response-map]
  (if-let [foreign-trade-id (:trade-id (:data response-map))]
    (when-let [other-identity (interchange-map-util/from-identity response-map)]
      (when-let [offer (find-offer initial-trade response-map)]
        (trade-model/set-foreign-trade-id (:id initial-trade) foreign-trade-id)))
    (trade-model/destroy-record initial-trade)))

(defn find-identity 
  ([offer] (find-identity (:name offer) (:public-key offer) (:public-key-algorithm offer)))
  ([user-name public-key public-key-algorithm]
    (identity-model/find-identity user-name public-key public-key-algorithm)))

(defn create-offer []
  (offer-model/create-new-offer
    { :use_id (:id (user-model/current-user))
      :closed 1 }))

(defn create-initial-trade [other-identity]
  (trade-model/create-acceptor-trade other-identity (create-offer)))

(defn create-data [offer initial-trade-id other-identity]
  { :name (:name other-identity)
    :public-key (:public_key other-identity)
    :public-key-algorithm (:public_key_algorithm other-identity)
    :offer offer
    :foreign-trade-id initial-trade-id })

(defn send-message [offer initial-trade-id other-identity]
  (identity-model/send-message other-identity action-keys/accept-offer-action-key
    (create-data offer initial-trade-id other-identity)))

(defn call [offer]
  (let [other-identity (identity-model/find-identity (:name offer) (:public-key offer) (:public-key-algorithm offer))
        initial-trade-id (create-initial-trade other-identity)]
    (update-trade (trade-model/get-record initial-trade-id) (send-message offer initial-trade-id other-identity))))