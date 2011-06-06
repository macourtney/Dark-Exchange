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

(defn create-offer-from [foreign-offer]
  { :use_id (:id (user-model/current-user))
    :foreign_offer_id (:id foreign-offer)
    :closed 1
    :has_amount (:wants_amount foreign-offer)
    :has_currency (:wants_currency foreign-offer)
    :has_payment_type (:wants_payment_type foreign-offer)
    :wants_amount (:has_amount foreign-offer)
    :wants_currency (:has_currency foreign-offer)
    :wants_payment_type (:has_payment_type foreign-offer) })

(defn update-offer [foreign-offer]
  (when foreign-offer
    (offer-model/update-or-create-offer (create-offer-from foreign-offer))))

(defn find-or-create-offer [response-map]
  (update-offer (:offer (:data response-map))))

(defn create-new-trade [response-map offer other-identity]
  (trade-model/create-acceptor-trade other-identity (:trade-id (:data response-map)) offer))

(defn create-trade [response-map]
  (when-let [other-identity (find-identity response-map)]
    (when-let [offer (find-or-create-offer response-map)]
      (create-new-trade response-map offer other-identity))))

(defn call [offer]
  (let [user-name (:name offer)
        public-key (:public-key offer)]
    (create-trade (identity-model/send-message user-name public-key action-keys/accept-offer-action-key
      { :name user-name :public-key public-key :offer offer }))))