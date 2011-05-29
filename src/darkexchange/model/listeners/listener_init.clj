(ns darkexchange.model.listeners.listener-init
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.i2p-server :as i2p-server-model]
            [darkexchange.model.listeners.identity-interceptor :as identity-interceptor]
            [darkexchange.model.listeners.peer :as peer-listeners]
            [darkexchange.model.server :as server-model]))

(defn init []
  (logging/info "Adding listeners.")
  (i2p-server-model/add-destination-listener peer-listeners/destination-listener)
  (server-model/add-server-receive-interceptor identity-interceptor/interceptor))