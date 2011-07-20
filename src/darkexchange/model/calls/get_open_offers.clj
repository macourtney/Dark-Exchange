(ns darkexchange.model.calls.get-open-offers
  (:require [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.identity :as identity-model]))

(defn call [identity]
  (:data (identity-model/send-message identity action-keys/get-open-offers-action-key
        { :identity identity })))