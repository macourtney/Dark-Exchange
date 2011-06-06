(ns darkexchange.model.calls.accept-offer
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.calls.util :as calls-util]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.model.trade :as trade-model]
            [darkexchange.model.user :as user-model]))

(defn find-identity [response-map]
  (identity-model/find-or-create-identity (calls-util/from-user-name response-map)
    (calls-util/from-public-key response-map) (calls-util/from-destination response-map)))

(defn create-offer-from [foreign-offer other-identity]
  { :identity_id (:id other-identity)
    :use_id (:id (user-model/current-user))
    :foreign_offer_id (:id foreign-offer)
    :has_amount (:wants_amount foreign-offer)
    :has_currency (:wants_currency foreign-offer)
    :has_payment_type (:wants_payment_type foreign-offer)
    :wants_amount (:has_amount foreign-offer)
    :wants_currency (:has_currency foreign-offer)
    :wants_payment_type (:has_payment_type foreign-offer) })

(defn update-offer [foreign-offer other-identity]
  (when (and foreign-offer other-identity)
    (offer-model/update-or-create-offer (create-offer-from foreign-offer other-identity))))

(defn find-or-create-offer [response-map]
  (update-offer (:offer (:data response-map)) (find-identity response-map)))

(defn create-new-trade [response-map offer]
  (trade-model/create-new-trade
    { :offer_id (:id offer) :foreign_trade_id (:trade-id (:data response-map)) :wants_first 0 }))

(defn create-trade [response-map]
  (when-let [offer (find-or-create-offer response-map)]
    (create-new-trade response-map offer)))

(defn call [offer]
  (let [user-name (:name offer)
        public-key (:public-key offer)]
    (create-trade (identity-model/send-message (:name offer) (:public-key offer) action-keys/accept-offer-action-key
      { :name user-name :public-key public-key :offer offer }))))