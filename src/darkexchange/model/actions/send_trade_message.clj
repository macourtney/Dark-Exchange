(ns darkexchange.model.actions.send-trade-message
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.interchange-map-util :as interchange-map-util]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.trade :as trade-model]
            [darkexchange.model.trade-message :as trade-message-model]))

(def action-key action-keys/send-trade-message-action-key)

(defn find-trade [trade-data from-identity]
  (trade-model/find-trade (:foreign-trade-id trade-data) from-identity))

(defn create-message [request-map]
  (let [data (:data request-map)
        trade-data (:trade data)
        from-identity (interchange-map-util/from-identity request-map)]
    (logging/debug (str "trade-data: " trade-data))
    (trade-message-model/create-new-message (:body trade-data) (find-trade trade-data from-identity)
      (:foreign-message-id trade-data) from-identity)))

(defn action [request-map]
  { :data { :foreign-message-id (create-message request-map) } })