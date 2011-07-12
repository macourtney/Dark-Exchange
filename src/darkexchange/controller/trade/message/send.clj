(ns darkexchange.controller.trade.message.send
  (:require [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.model.calls.send-trade-message :as send-trade-message-call]
            [darkexchange.view.trade.message.send :as send-view]
            [seesaw.core :as seesaw-core]))

(defn attach-cancel-action [parent-component]
  (actions-utils/attach-window-close-listener parent-component "#cancel-button"))

(defn find-message-body-text [parent-component]
  (seesaw-core/select parent-component ["#message-body-text"]))

(defn message-body [parent-component]
  (seesaw-core/text (find-message-body-text parent-component)))

(defn send-message [parent-component trade message]
  (future
    (send-trade-message-call/call trade message))
  (actions-utils/close-window (seesaw.core/to-frame parent-component)))

(defn send-action [trade parent-component _]
  (controller-utils/disable-widget parent-component)
  (send-message parent-component trade (message-body parent-component)))

(defn attach-send-action [trade parent-component]
  (actions-utils/attach-frame-listener parent-component "#send-button" #(send-action trade %1 %2)))

(defn attach [trade parent-component]
  (attach-send-action trade (attach-cancel-action parent-component)))

(defn show [trade-frame trade]
  (controller-utils/show (attach trade (send-view/create trade-frame))))