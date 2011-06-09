(ns darkexchange.model.listeners.header-reply-interceptor
  (:require [darkexchange.model.i2p-server :as i2p-server]
            [darkexchange.model.user :as user-model]))

(defn response-map-user []
  (let [user (user-model/current-user)]
    { :name (:name user) :public-key (:public_key user) :public-key-algorithm (:public_key_algorithm user) }))

(defn response-map-from []
  { :destination (i2p-server/base-64-destination) :user (response-map-user) })

(defn interceptor [action response-map]
  (merge { :from (response-map-from) :type :ok } (action response-map)))