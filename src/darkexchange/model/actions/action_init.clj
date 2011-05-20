(ns darkexchange.model.actions.action-init
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.server :as server]
            [darkexchange.model.peer :as peer-model]))

(defn init []
  (logging/debug (str "Adding notify action."))
  (server/add-action peer-model/notify-action-key peer-model/notify-action)
  (server/add-action peer-model/get-peers-action-key peer-model/get-peers-action))