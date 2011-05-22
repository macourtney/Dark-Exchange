(ns darkexchange.model.model-init
  (:require [darkexchange.model.actions.action-init :as action-init]
            [darkexchange.model.listeners.listener-init :as listener-init]
            [darkexchange.model.property :as property]))

(defn init []
  (property/load-properties)
  (listener-init/init)
  (action-init/init))