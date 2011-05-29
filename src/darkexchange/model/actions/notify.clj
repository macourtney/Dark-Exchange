(ns darkexchange.model.actions.notify
  (:require [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.peer :as peer-model]))

(def action-key action-keys/notify-action-key)

(defn action [request-map]
  (peer-model/update-destination (:destination (:data request-map)))
  "ok")