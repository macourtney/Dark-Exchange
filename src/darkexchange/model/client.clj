(ns darkexchange.model.client
  (:require [darkexchange.model.i2p-server :as i2p-server-model]
            [darkexchange.model.user :as user-model]))

(defn current-destination []
  (i2p-server-model/current-destination))

(defn base-64-destination []
  (i2p-server-model/base-64-destination))

(defn request-map-user []
  (let [user (user-model/current-user)]
    { :name (:name user) :public-key (:public_key user) }))

(defn request-map-from []
  { :destination (base-64-destination) :user (request-map-user) })

(defn create-request-map [action data]
  { :action action :data data :from (request-map-from) })

(defn send-message [destination action data]
  (i2p-server-model/send-message destination (create-request-map action data)))