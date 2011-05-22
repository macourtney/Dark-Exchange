(ns darkexchange.model.listeners.peer
  (:require [darkexchange.model.peer :as peer-model]))

(defn destination-listener [destination]
  (peer-model/download-peers))