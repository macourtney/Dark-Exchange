(ns darkexchange.model.calls.update-trades
  (:require [darkexchange.interchange-map-util :as interchange-map-util]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.trade :as trade-model]))

(defn create-call-data [trade]
  { :id (:id trade)
    :foreign-trade-id (:foreign_trade_id trade)
    :accept-confirm (:accept_confirm trade)
    :wants-first (:wants_first trade)
    :wants-received (:wants_received trade)
    :has-sent (:has_sent trade)
    :closed (:closed trade) })

(defn get-response-trade [response-map]
  (when-let [foreign-trade (:data response-map)]
    { :id (:id foreign-trade)
      :foreign_trade_id (:foreign-trade-id foreign-trade)
      :accept_confirm (:accept-confirm foreign-trade)
      :wants_first (:wants-first foreign-trade)
      :wants_received (:wants-received foreign-trade)
      :has_sent (:has-sent foreign-trade)
      :closed (:closed foreign-trade) }))

(defn save-updated-trade [response-map]
  (when-let [foreign-trade (get-response-trade response-map)]
    (trade-model/update-trade (interchange-map-util/from-identity response-map) foreign-trade)))

(defn update-trade [trade]
  (save-updated-trade
    (identity-model/send-message (trade-model/find-identity trade) action-keys/update-trades-action-key
      (create-call-data trade))))

(defn call []
  (map update-trade (trade-model/open-trades)))