(ns darkexchange.model.interceptors.server-interceptors
  (:require [darkexchange.model.interceptors.interceptor-util :as interceptor-util]))

(def interceptors (atom []))

(defn add-interceptor [interceptor]
  (swap! interceptors #(cons interceptor %)))

(defn run-interceptors [function request-map]
  (interceptor-util/run-interceptors @interceptors function request-map))