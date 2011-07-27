(ns darkexchange.controller.trade.message.view
  (:require [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.model.trade-message :as trade-message-model]
            [darkexchange.view.trade.message.view :as view-view]
            [seesaw.core :as seesaw-core]))

(defn find-message-body-text [parent-component]
  (seesaw-core/select parent-component ["#message-body-text"]))

(defn load-data [message parent-component]
  (.setText (find-message-body-text parent-component) (:body message))
  (future
    (trade-message-model/mark-as-viewed message)
    (trade-message-model/mark-as-seen message))
  parent-component)

(defn attach-cancel-action [parent-component]
  (actions-utils/attach-window-close-listener parent-component "#cancel-button"))

(defn attach [message parent-component]
  (attach-cancel-action parent-component))

(defn show [trade-frame message-id]
  (when-let [message (trade-message-model/get-record message-id)]
    (controller-utils/show (attach message (load-data message (view-view/create trade-frame))))))