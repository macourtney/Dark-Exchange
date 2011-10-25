(ns darkexchange.controller.identity.view
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.controller.offer.open-offer-table :as open-offer-table-controller]
            [darkexchange.controller.offer.view :as offer-view-controller]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.controller.widgets.utils :as widgets-utils]
            [darkexchange.model.calls.get-open-offers :as get-open-offers-call]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.model.terms :as terms]
            [darkexchange.model.trust-score :as trust-score-model]
            [darkexchange.view.identity.view :as identity-view]
            [seesaw.core :as seesaw-core]))

(def identity-propery-name "darkexchange.identity")

(defn property-widget [parent-component]
  (.getRootPane (seesaw-core/to-frame parent-component)))

(defn set-identity [parent-component identity]
  (.putClientProperty (property-widget parent-component) identity-propery-name identity))

(defn get-identity [parent-component]
  (.getClientProperty (property-widget parent-component) identity-propery-name))

(defn find-name-label [parent-component]
  (controller-utils/find-component parent-component "#name-label"))

(defn find-public-key-label [parent-component]
  (controller-utils/find-component parent-component "#public-key-label"))

(defn find-algorithm-label [parent-component]
  (controller-utils/find-component parent-component "#public-key-algorithm-label"))

(defn find-is-online-label [parent-component]
  (controller-utils/find-component parent-component "#is-online-label"))

(defn find-status-label [parent-component]
  (controller-utils/find-component parent-component "#offer-table-status-label"))

(defn find-my-trust-score-label [parent-component]
  (controller-utils/find-component parent-component "#my-trust-score-label"))

(defn find-network-trust-score-label [parent-component]
  (controller-utils/find-component parent-component "#network-trust-score-label"))

(defn find-view-offer-button [parent-component]
  (seesaw-core/select parent-component ["#view-offer-button"]))

(defn find-trust-score-slider [parent-component]
  (controller-utils/find-component parent-component "#trust-score-slider"))

(defn attach-cancel-action [parent-component]
  (actions-utils/attach-window-close-listener parent-component "#cancel-button"))

(defn selected-offer [parent-component]
  (open-offer-table-controller/selected-offer parent-component))

(defn original-selected-offer [parent-component]
  (:original-offer (selected-offer parent-component)))

(defn selected-view-offer [parent-component]
  (let [original-offer (original-selected-offer parent-component)
        identity (:identity original-offer)]
    { :id (:id original-offer)
      :public-key (:public_key identity)
      :public-key-algorithm (:public_key_algorithm identity)
      :name (:name identity)
      :wants (offer-model/wants-amount-str original-offer)
      :to_receive_by (offer-model/wants-payment-type-str original-offer)
      :has (offer-model/has-amount-str original-offer)
      :to_send_by (offer-model/has-payment-type-str original-offer) }))

(defn view-offer-listener [parent-component]
  (offer-view-controller/show parent-component
    (selected-view-offer parent-component)))

(defn attach-view-offer-action [parent-component]
  (actions-utils/attach-listener parent-component "#view-offer-button"
    (fn [_] (view-offer-listener parent-component))))

(defn view-offer-if-enabled [parent-component]
  (widgets-utils/do-click-if-enabled (find-view-offer-button parent-component)))

(defn attach-view-offer-table-action [parent-component]
  (open-offer-table-controller/attach-table-action parent-component #(view-offer-if-enabled parent-component)))

(defn attach-view-offer-button-enable-listener [parent-component]
  (open-offer-table-controller/attach-single-select-button-enable-listener parent-component
    (find-view-offer-button parent-component)))

(defn load-my-trust-score [parent-component trust-score]
  (seesaw-core/config! (find-my-trust-score-label parent-component)
    :text (trust-score-model/basic-percent-int trust-score)))

(defn trust-score-slider-state-change-listener [e]
  (let [slider (seesaw-core/to-widget e)
        parent-component (seesaw-core/to-frame slider)]
    (when (not (.getValueIsAdjusting slider))
      (let [trust-score (/ (.getValue slider) 100.0)]
        (trust-score-model/set-trust-score (get-identity parent-component) trust-score)
        (load-my-trust-score parent-component { :basic trust-score })))))

(defn attach-trust-score-slider-listener [parent-component]
  (seesaw-core/listen (find-trust-score-slider parent-component)
    :state-changed trust-score-slider-state-change-listener)
  parent-component)

(defn attach-view-offer-actions [parent-component]
  (attach-view-offer-button-enable-listener
    (attach-view-offer-table-action
      (attach-view-offer-action parent-component))))

(defn attach [parent-component identity]
  (attach-trust-score-slider-listener (attach-view-offer-actions (attach-cancel-action parent-component))))

(defn load-name [parent-component identity]
  (seesaw-core/config! (find-name-label parent-component) :text (:name identity)))

(defn load-public-key [parent-component identity]
  (seesaw-core/config! (find-public-key-label parent-component)
    :text (identity-model/shortened-public-key identity)))

(defn load-algorithm [parent-component identity]
  (seesaw-core/config! (find-algorithm-label parent-component)
    :text (:public_key_algorithm identity)))

(defn load-is-online [parent-component identity]
  (seesaw-core/config! (find-is-online-label parent-component)
    :text (if (identity-model/is-online? identity) (terms/yes) (terms/no))))

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
    (when-let [open-offers (get-open-offers-call/call (identity-model/get-record (:id identity)))]
      (logging/warn (str "(.getClass open-offers): " (.getClass open-offers)))
      (logging/warn (str "open-offers: " open-offers))
      (seesaw-core/invoke-later (set-offers parent-component (map #(assoc % :identity identity) open-offers))))))

(defn load-network-trust-score [parent-component trust-score]
  (seesaw-core/config! (find-network-trust-score-label parent-component)
    :text (trust-score-model/combined-percent-int trust-score)))

(defn load-trust-score-slider [parent-component trust-score]
  (seesaw-core/config! (find-trust-score-slider parent-component)
    :value (trust-score-model/basic-percent-int trust-score)))

(defn load-trust-scores [parent-component identity]
  (let [trust-score (or (trust-score-model/find-trust-score identity) { :basic 0.0 :combined 0.0 })]
    (load-my-trust-score parent-component trust-score)
    (load-network-trust-score parent-component trust-score)
    (load-trust-score-slider parent-component trust-score)))

(defn load-data [parent-component identity]
  (set-identity parent-component identity)
  (load-name parent-component identity)
  (load-public-key parent-component identity)
  (load-algorithm parent-component identity)
  (load-is-online parent-component identity)
  (load-offers parent-component identity)
  (load-trust-scores parent-component identity)
  parent-component)

(defn create-and-initialize [main-frame identity]
  (attach (load-data (identity-view/create main-frame) identity) identity))

(defn show [main-frame identity]
  (controller-utils/show (create-and-initialize main-frame identity)))