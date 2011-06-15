(ns darkexchange.model.listeners.trade
  (:require [darkexchange.model.calls.update-trades :as update-trades-call]))

(defn update-trades-in-background []
  (update-trades-call/call ))

(defn destination-listener [destination]
  (.start (Thread. update-trades-in-background)))