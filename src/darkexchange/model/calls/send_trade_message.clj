(ns darkexchange.model.calls.send-trade-message
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.trade :as trade-model]
            [darkexchange.model.trade-message :as trade-message-model]))

(defn create-message [trade message-body]
  (trade-message-model/create-message-to-send message-body trade))

(defn create-trade-data [message-id]
  (let [message (trade-message-model/get-record message-id)]
    { :body (:body message) :foreign-trade-id (:trade_id message) :foreign-message-id (:id message) }))

(defn update-foreign-message-id [message-id response-map]
  (when-let [foreign-message-id (:foreign-message-id (:data response-map))]
    (trade-message-model/update-foreign-message-id message-id foreign-message-id)))

(defn call [trade message-body]
  (let [message-id (create-message trade message-body)]
    (update-foreign-message-id message-id
      (identity-model/send-message (trade-model/find-identity trade) action-keys/send-trade-message-action-key
        { :trade (create-trade-data message-id) }))))