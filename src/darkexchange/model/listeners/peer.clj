(ns darkexchange.model.listeners.peer
  (:require [darkexchange.model.peer :as peer-model]))

(defn destination-listener [destination]
  (peer-model/add-local-destination destination)
  (peer-model/download-peers))