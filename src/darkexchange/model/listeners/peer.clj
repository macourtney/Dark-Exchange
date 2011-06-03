(ns darkexchange.model.listeners.peer
  (:require [darkexchange.model.peer :as peer-model]))

(defn destination-listener [destination]
  (peer-model/download-peers))

(defn remove-missing-peers [destination _]
  (peer-model/remove-destination destination))