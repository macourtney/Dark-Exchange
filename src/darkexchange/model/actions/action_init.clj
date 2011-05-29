(ns darkexchange.model.actions.action-init
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.get-peers :as get-peers]
            [darkexchange.model.actions.notify :as notify]
            [darkexchange.model.server :as server-model]))

(defn init []
  (logging/info (str "Adding actions."))
  (server-model/add-action notify/action-key notify/action)
  (server-model/add-action get-peers/action-key get-peers/action))