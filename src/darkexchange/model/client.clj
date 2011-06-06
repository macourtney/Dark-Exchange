(ns darkexchange.model.client
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.i2p-server :as i2p-server-model]
            [darkexchange.model.user :as user-model]
            [darkexchange.model.util :as model-util]))

(def response-interceptors (atom []))

(defn add-response-interceptor [interceptor]
  (swap! response-interceptors conj interceptor))

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

(defn run-response-interceptors [response-map]
  (model-util/run-interceptors @response-interceptors response-map))

(defn send-message [destination action data]
  (run-response-interceptors (i2p-server-model/send-message destination (create-request-map action data))))

(defn send-messages [destinations action data call-back]
  (doseq [destination destinations]
    (.start (Thread. #(call-back (send-message destination action data))))))