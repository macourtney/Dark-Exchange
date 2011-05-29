(ns darkexchange.model.listeners.listener-init
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.i2p-server :as i2p-server]
            [darkexchange.model.listeners.peer :as peer-listeners]))

(defn init []
  (logging/info "Adding listeners.")
  (i2p-server/add-destination-listener peer-listeners/destination-listener))