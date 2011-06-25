(ns darkexchange.model.calls.notify
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.client :as client]
            [darkexchange.model.i2p-server :as i2p-server]))

(defn call [destination]
  (try
    (let [response (client/send-message (i2p-server/as-destination destination) action-keys/notify-action-key
                     { :destination (client/base-64-destination) })]
      (= (:data response) "ok"))
    (catch Exception e
      (logging/error (str "e: " e) e)
      nil)))