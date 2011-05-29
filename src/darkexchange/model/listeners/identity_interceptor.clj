(ns darkexchange.model.listeners.identity-interceptor
  (:require [darkexchange.model.identity :as identity-model]))

(defn identity-updater [request-map]
  (let [from-map (:from request-map)
        destination (:destination from-map)
        user-map (:user from-map)
        user-name (:name user-map)
        public-key (:public-key user-map)]
    (identity-model/add-or-update-identity user-name public-key destination)))

(defn interceptor [request-map]
  (.start (Thread. #(identity-updater request-map)))
  request-map)