(ns darkexchange.model.client
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.i2p-server :as i2p-server-model]
            [darkexchange.model.interceptors.client-interceptors :as client-interceptors]
            [darkexchange.model.user :as user-model]))

(defn current-destination []
  (i2p-server-model/current-destination))

(defn base-64-destination []
  (i2p-server-model/base-64-destination))

(defn request-map-user []
  (let [user (user-model/current-user)]
    { :name (:name user) :public-key (:public_key user) :public-key-algorithm (:public_key_algorithm user) }))

(defn request-map-from []
  { :destination (base-64-destination) :user (request-map-user) })

(defn create-request-map [destination action data]
  { :destination destination :action action :data data :from (request-map-from) })

(defn- send-request [request-map]
  (try
    (i2p-server-model/send-message (:destination request-map) (dissoc request-map :destination))
    (catch java.net.NoRouteToHostException e
      (logging/warn (str "Could not connect to destination: " (:destination request-map)))
      nil)))

(defn send-message [destination action data]
  (client-interceptors/run-interceptors send-request (create-request-map destination action data)))

(defn send-messages [destinations action data call-back]
  (doall (map #(future (call-back (send-message % action data))) destinations)))