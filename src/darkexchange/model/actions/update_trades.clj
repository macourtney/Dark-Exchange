(ns darkexchange.model.actions.update-trades
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.interchange-map-util :as interchange-map-util]
            [darkexchange.model.calls.update-trades :as update-trades-call]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.trade :as trade-model]))

(def action-key action-keys/update-trades-action-key)

(defn get-incoming-message [message]
  { :id (:id message)
    :foreign_trade_id (:foreign-trade-id message)
    :body (:body message) })

(defn get-incoming-trade [request-map]
  (when-let [trade-data (:data request-map)]
    { :id (:id trade-data)
      :foreign_trade_id (:foreign-trade-id trade-data)
      :accept_confirm (:accept-confirm trade-data)
      :wants_first (:wants-first trade-data)
      :wants_received (:wants-received trade-data)
      :has_sent (:has-sent trade-data)
      :closed (:closed trade-data)
      :messages (map get-incoming-message (:messages trade-data)) }))

(defn update-trade [request-map]
  (trade-model/update-trade (interchange-map-util/from-identity request-map) (get-incoming-trade request-map)))

(defn generate-response-message [message]
  (when message
    { :id (:id message)
      :foreign-message-id (:foreign_message_id message)
      :body (:body message) }))

(defn generate-response [trade]
  (if trade
    { :data
      { :id (:id trade)
        :foreign-trade-id (:foreign_trade_id trade)
        :accept-confirm (:accept_confirm trade)
        :wants-first (:wants_first trade)
        :wants-received (:wants_received trade)
        :has-sent (:has_sent trade)
        :closed (:closed trade)
        :messages (filter identity (map generate-response-message (concat (:messages trade) (trade-model/unconfirmed-messages trade)))) } }
    { :type :trade-not-found :data "Trade Not found."}))

(defn action [request-map]
  (let [trade (update-trade request-map)
        response (generate-response trade)]
    (when (trade-model/contains-unconfirmed-message? trade)
      (update-trades-call/update-trade trade))
    response))