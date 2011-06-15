(ns darkexchange.model.listeners.listener-init
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.client :as client-model]
            [darkexchange.model.i2p-server :as i2p-server-model]
            [darkexchange.model.interceptors.client-interceptors :as client-interceptors]
            [darkexchange.model.interceptors.header-reply-interceptor :as header-reply-interceptor]
            [darkexchange.model.interceptors.identity-interceptor :as identity-interceptor]
            [darkexchange.model.interceptors.server-interceptors :as server-interceptors]
            [darkexchange.model.interceptors.signature-interceptor :as signature-interceptor]
            [darkexchange.model.listeners.peer :as peer-listeners]
            [darkexchange.model.listeners.trade :as trade-listener]
            [darkexchange.model.server :as server-model]))

(defn client-init []
  ; These apply in the order of bottom first. Like an upside-down stack.
  (client-interceptors/add-interceptor identity-interceptor/client-interceptor)
  (client-interceptors/add-interceptor signature-interceptor/client-interceptor))

(defn server-init []
  ; These apply in the order of bottom first. Like an upside-down stack.
  (server-interceptors/add-interceptor identity-interceptor/server-interceptor)
  (server-interceptors/add-interceptor header-reply-interceptor/interceptor)
  (server-interceptors/add-interceptor signature-interceptor/server-interceptor))

(defn init []
  (logging/info "Adding listeners.")
  (i2p-server-model/add-destination-listener peer-listeners/destination-listener)
  (i2p-server-model/add-send-message-fail-listener peer-listeners/remove-missing-peers)
  (i2p-server-model/add-destination-listener trade-listener/destination-listener)
  (client-init)
  (server-init))