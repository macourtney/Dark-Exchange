(ns darkexchange.model.listeners.self-identity-listener
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.i2p-server :as i2p-server-model]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.user :as user]))

(defn destination-listener [destination]
  (when-let [user (user/current-user)]
    (identity-model/add-or-update-identity (:name user) (:public_key user) (:public_key_algorithm user)
      (i2p-server-model/current-destination))))