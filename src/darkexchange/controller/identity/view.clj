(ns darkexchange.controller.identity.view
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.controller.offer.open-offer-table :as open-offer-table-controller]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.model.calls.get-open-offers :as get-open-offers-call]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.model.terms :as terms]
            [darkexchange.view.identity.view :as identity-view]
            [seesaw.core :as seesaw-core]))

(defn attach-cancel-action [parent-component]
  (actions-utils/attach-window-close-listener parent-component "#cancel-button"))

(defn attach [parent-component offer]
  (attach-cancel-action parent-component))

(defn find-name-label [parent-component]
  (controller-utils/find-component parent-component "#name-label"))

(defn load-name [parent-component identity]
  (seesaw-core/config! (find-name-label parent-component) :text (:name identity)))

(defn find-public-key-label [parent-component]
  (controller-utils/find-component parent-component "#public-key-label"))

(defn load-public-key [parent-component identity]
  (seesaw-core/config! (find-public-key-label parent-component)
    :text (identity-model/shortened-public-key identity)))

(defn find-algorithm-label [parent-component]
  (controller-utils/find-component parent-component "#public-key-algorithm-label"))

(defn load-algorithm [parent-component identity]
  (seesaw-core/config! (find-algorithm-label parent-component)
    :text (:public_key_algorithm identity)))

(defn find-is-online-label [parent-component]
  (controller-utils/find-component parent-component "#is-online-label"))

(defn load-is-online [parent-component identity]
  (seesaw-core/config! (find-is-online-label parent-component)
    :text (if (identity-model/is-online? identity) (terms/yes) (terms/no))))

(defn find-status-label [parent-component]
  (controller-utils/find-component parent-component "#offer-table-status-label"))

(defn set-table-status [parent-component status-text]
  (seesaw-core/config! (find-status-label parent-component) :text (terms/status-parens status-text)))

(defn set-complete-table-status [parent-component]
  (seesaw-core/config! (find-status-label parent-component) :text ""))

(defn set-offers [parent-component offers]
  (if offers
    (do
      (open-offer-table-controller/load-open-offer-table parent-component (map offer-model/convert-to-table-offer offers))
      (set-complete-table-status parent-component))
    (set-table-status parent-component (terms/error-downloading-offers))))

(defn load-offers [parent-component identity]
  (future
    (let [open-offers (get-open-offers-call/call (identity-model/get-record (:id identity)))]
      (seesaw-core/invoke-later (set-offers parent-component open-offers)))))

(defn load-data [parent-component identity]
  (load-name parent-component identity)
  (load-public-key parent-component identity)
  (load-algorithm parent-component identity)
  (load-is-online parent-component identity)
  (load-offers parent-component identity)
  parent-component)

(defn show [main-frame identity]
  (controller-utils/show (attach (load-data (identity-view/create main-frame) identity) identity)))