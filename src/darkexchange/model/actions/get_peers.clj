(ns darkexchange.model.actions.get-peers
  (:require [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.peer :as peer-model]))

(def action-key action-keys/get-peers-action-key)

(defn action [request-map]
  { :data (map :destination (peer-model/all-peers)) })