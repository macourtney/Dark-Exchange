(ns darkexchange.model.client
  (:require [darkexchange.model.i2p-server :as i2p-server]))

(defn create-request-map [action data]
  { :action action :data data :from-destination (i2p-server/base-64-destination) })

(defn send-message [destination action data]
  (let [from-server (i2p-server/send-message destination (create-request-map action data))]
    (println (str "Response type: " (:type from-server) " Received from server: \"" (:data from-server) "\""))))