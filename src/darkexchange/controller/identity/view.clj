(ns darkexchange.controller.identity.view
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.model.identity :as identity-model]
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

(defn load-data [parent-component identity]
  (load-name parent-component identity)
  (load-public-key parent-component identity)
  (load-algorithm parent-component identity)
  (load-is-online parent-component identity)
  parent-component)

(defn show [main-frame identity]
  (controller-utils/show (attach (load-data (identity-view/create main-frame) identity) identity)))