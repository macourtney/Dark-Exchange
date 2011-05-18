(ns darkexchange.model.client
  (:require [darkexchange.model.i2p-server :as i2p-server]))

(defn current-destination []
  (i2p-server/current-destination))

(defn base-64-destination []
  (i2p-server/base-64-destination))

(defn create-request-map [action data]
  { :action action :data data :from-destination (base-64-destination) })

(defn send-message [destination action data]
  (i2p-server/send-message destination (create-request-map action data)))