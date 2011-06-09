(ns darkexchange.model.listeners.server-interceptors
  (:require [darkexchange.model.listeners.interceptor-util :as interceptor-util]))

(def interceptors (atom []))

(defn add-interceptor [interceptor]
  (swap! interceptors conj interceptor))

(defn run-interceptors [function request-map]
  (interceptor-util/run-interceptors @interceptors function request-map))