(ns darkexchange.model.listeners.identity-interceptor
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.identity :as identity-model]))

(defn identity-updater [interceptor-map]
  (let [from-map (:from interceptor-map)
        user-map (:user from-map)]
    (identity-model/add-or-update-identity (:name user-map) (:public-key user-map) (:destination from-map))))

(defn interceptor [interceptor-map]
  (.start (Thread. #(identity-updater interceptor-map)))
  interceptor-map)