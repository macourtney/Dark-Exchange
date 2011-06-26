(ns darkexchange.model.interceptors.identity-interceptor
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.identity :as identity-model]))

(defn identity-updater [interceptor-map]
  (when-let [from-map (:from interceptor-map)]
    (let [user-map (:user from-map)]
      (identity-model/add-or-update-identity (:name user-map) (:public-key user-map) (:public-key-algorithm user-map)
        (:destination from-map)))))

(defn server-interceptor [action request-map]
  (let [response-map (action request-map)]
    (.start (Thread. #(identity-updater request-map)))
    response-map))

(defn client-interceptor [action request-map]
  (let [response-map (action request-map)]
    (.start (Thread. #(identity-updater response-map)))
    response-map))