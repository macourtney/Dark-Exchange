(ns darkexchange.model.listeners.listener-init
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.client :as client-model]
            [darkexchange.model.i2p-server :as i2p-server-model]
            [darkexchange.model.listeners.client-interceptors :as client-interceptors]
            [darkexchange.model.listeners.header-reply-interceptor :as header-reply-interceptor]
            [darkexchange.model.listeners.identity-interceptor :as identity-interceptor]
            [darkexchange.model.listeners.peer :as peer-listeners]
            [darkexchange.model.listeners.server-interceptors :as server-interceptors]
            [darkexchange.model.server :as server-model]))

(defn client-init []
  (client-interceptors/add-interceptor identity-interceptor/client-interceptor))

(defn server-init []
  (server-interceptors/add-interceptor identity-interceptor/server-interceptor)
  (server-interceptors/add-interceptor header-reply-interceptor/interceptor))

(defn init []
  (logging/info "Adding listeners.")
  (i2p-server-model/add-destination-listener peer-listeners/destination-listener)
  (i2p-server-model/add-send-message-fail-listener peer-listeners/remove-missing-peers)
  (client-init)
  (server-init))