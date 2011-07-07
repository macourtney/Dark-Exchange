(ns darkexchange.controller.offer.view
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.model.calls.accept-offer :as accept-offer-call]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.view.offer.view :as offer-view]
            [seesaw.core :as seesaw-core]))

(defn attach-cancel-action [parent-component]
  (actions-utils/attach-window-close-listener parent-component "#cancel-button"))

(defn accept-offer-action [parent-component offer]
  (accept-offer-call/call offer)
  (actions-utils/close-window parent-component))

(defn attach-accept-offer-action [parent-component offer]
  (actions-utils/attach-listener parent-component "#accept-offer-button"
    (fn [_] (accept-offer-action parent-component offer))))

(defn attach [parent-component offer]
  (attach-accept-offer-action (attach-cancel-action parent-component) offer))

(defn find-public-key-label [parent-component]
  (controller-utils/find-component parent-component "#public-key-label"))

(defn load-public-key [parent-component offer]
  (seesaw-core/config! (find-public-key-label parent-component)
    :text (identity-model/shortened-public-key-str (:public-key offer))))

(defn find-name-label [parent-component]
  (controller-utils/find-component parent-component "#name-label"))

(defn load-name [parent-component offer]
  (seesaw-core/config! (find-name-label parent-component) :text (:name offer)))

(defn find-wants-label [parent-component]
  (controller-utils/find-component parent-component "#wants-label"))

(defn load-wants [parent-component offer]
  (seesaw-core/config! (find-wants-label parent-component) :text (:wants offer)))

(defn find-to-receive-by-label [parent-component]
  (controller-utils/find-component parent-component "#to-receive-by-label"))

(defn load-to-receive-by [parent-component offer]
  (seesaw-core/config! (find-to-receive-by-label parent-component) :text (:to_receive_by offer)))

(defn find-has-label [parent-component]
  (controller-utils/find-component parent-component "#has-label"))

(defn load-has [parent-component offer]
  (seesaw-core/config! (find-has-label parent-component) :text (:has offer)))

(defn find-to-send-by-label [parent-component]
  (controller-utils/find-component parent-component "#to-send-by-label"))

(defn load-to-send-by [parent-component offer]
  (seesaw-core/config! (find-to-send-by-label parent-component) :text (:to_send_by offer)))

(defn load-data [parent-component offer]
  (load-public-key parent-component offer)
  (load-name parent-component offer)
  (load-wants parent-component offer)
  (load-to-receive-by parent-component offer)
  (load-has parent-component offer)
  (load-to-send-by parent-component offer)
  parent-component)

(defn show [main-frame offer]
  (controller-utils/show (attach (load-data (offer-view/create main-frame) offer) offer)))