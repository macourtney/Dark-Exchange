(ns darkexchange.model.listeners.listener-init
  (:require [darkexchange.model.i2p-server :as i2p-server]
            [darkexchange.model.listeners.peer :as peer-listeners]))

(defn init []
  (i2p-server/add-destination-listener peer-listeners/destination-listener))