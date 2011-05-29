(ns darkexchange.model.listeners.identity-interceptor
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.identity :as identity-model]))

(defn identity-updater [request-map]
  (let [from-map (:from request-map)
        user-map (:user from-map)]
    (identity-model/add-or-update-identity (:name user-map) (:public-key user-map) (:destination from-map))))

(defn interceptor [request-map]
  (.start (Thread. #(identity-updater request-map)))
  request-map)