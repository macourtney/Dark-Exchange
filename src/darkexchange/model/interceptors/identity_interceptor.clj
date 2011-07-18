(ns darkexchange.model.interceptors.identity-interceptor
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.identity :as identity-model]))

(defn identity-is-online [interceptor-map]
  (when-let [from-map (:from interceptor-map)]
    (let [user-map (:user from-map)]
      (identity-model/add-or-update-identity (:name user-map) (:public-key user-map) (:public-key-algorithm user-map)
        (:destination from-map)))))

(defn identity-is-offline [target-destination]
  (identity-model/identity-not-online target-destination))

(defn identity-updater [interceptor-map target-destination]
  (if interceptor-map
    (identity-is-online interceptor-map)
    (identity-is-offline target-destination)))

(defn server-interceptor [action request-map]
  (let [target-destination (:destination (:from request-map))
        response-map (action request-map)]
    (.start (Thread. #(identity-updater request-map target-destination)))
    response-map))

(defn client-interceptor [action request-map]
  (let [target-destination (:destination request-map)
        response-map (action request-map)]
    (.start (Thread. #(identity-updater response-map target-destination)))
    response-map))