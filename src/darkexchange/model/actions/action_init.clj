(ns darkexchange.model.actions.action-init
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.accept-offer :as accept-offer]
            [darkexchange.model.actions.confirm-trade :as confirm-trade]
            [darkexchange.model.actions.get-open-offers :as get-open-offers]
            [darkexchange.model.actions.get-peers :as get-peers]
            [darkexchange.model.actions.notify :as notify]
            [darkexchange.model.actions.payment-sent :as payment-sent]
            [darkexchange.model.actions.search-offers :as search-offers]
            [darkexchange.model.server :as server-model]))

(defn init []
  (logging/info "Adding actions.")
  (server-model/add-action accept-offer/action-key accept-offer/action)
  (server-model/add-action confirm-trade/action-key confirm-trade/action)
  (server-model/add-action get-open-offers/action-key get-open-offers/action)
  (server-model/add-action get-peers/action-key get-peers/action)
  (server-model/add-action notify/action-key notify/action)
  (server-model/add-action payment-sent/action-key payment-sent/action)
  (server-model/add-action search-offers/action-key search-offers/action))