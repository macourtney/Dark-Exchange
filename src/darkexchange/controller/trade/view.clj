(ns darkexchange.controller.trade.view
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.model.calls.confirm-trade :as confirm-trade-call]
            [darkexchange.model.calls.payment-received :as payment-received-call]
            [darkexchange.model.calls.payment-sent :as payment-sent-call]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.model.terms :as terms]
            [darkexchange.model.trade :as trade-model]
            [darkexchange.view.trade.view :as view-view]
            [seesaw.core :as seesaw-core]))

(defn load-data-label 
  ([trade parent-component trade-key label-key] (load-data-label (trade-key trade) parent-component label-key))
  ([value parent-component label-key]
    (seesaw-core/config! (seesaw-core/select parent-component [label-key]) :text value)
    parent-component))

(defn load-trade-id [trade parent-component]
  (load-data-label trade parent-component :id "#id"))

(defn load-initiated-at [trade parent-component]
  (load-data-label trade parent-component :created_at "#created_at"))

(defn load-trade-partner [trade parent-component]
  (load-data-label (str (:name (:identity trade)) " (" (.substring (:public_key (:identity trade)) 0 10) "..)")
    parent-component "#user"))

(defn load-partner-trade-id [trade parent-component]
  (load-data-label trade parent-component :foreign_trade_id "#foreign-trade-id"))

(defn load-partner-data [trade parent-component]
  (load-trade-partner trade (load-partner-trade-id trade parent-component)))

(defn load-im-sending-amount [trade parent-component]
  (load-data-label (offer-model/has-amount-str (:offer trade))
    parent-component "#im-sending-amount"))

(defn load-im-sending-by [trade parent-component]
  (load-data-label (offer-model/has-payment-type-str (:offer trade))
    parent-component "#im-sending-by"))

(defn load-im-receiving-amount [trade parent-component]
  (load-data-label (offer-model/wants-amount-str (:offer trade))
    parent-component "#im-receiving-amount"))

(defn load-im-receiving-by [trade parent-component]
  (load-data-label (offer-model/wants-payment-type-str (:offer trade))
    parent-component "#im-receiving-by"))

(defn load-offer-data [trade parent-component]
  (load-im-receiving-by trade
    (load-im-receiving-amount trade
      (load-im-sending-by trade
        (load-im-sending-amount trade parent-component)))))

(defn load-waiting-for [trade parent-component]
  (load-data-label (trade-model/next-step-text trade)
    parent-component "#waiting-for"))

(defn load-data [parent-component trade]
  (load-waiting-for trade
    (load-offer-data trade
      (load-partner-data trade
          (load-initiated-at trade
            (load-trade-id trade parent-component))))))

(defn attach-cancel-action [parent-component]
  (actions-utils/attach-window-close-listener parent-component "#cancel-button"))

(defn disable-next-step-button [e]
  (seesaw-core/config! (controller-utils/find-component (seesaw-core/to-frame e) "#next-step-button") :enabled? false))

(defn execute-next-step-call [trade e call]
  (call trade)
  (disable-next-step-button e))

(defn attach-next-step-action [parent-component trade action button-text]
  (actions-utils/attach-listener parent-component "#next-step-button" #(action trade %))
  (let [next-step-button (controller-utils/find-component parent-component "#next-step-button")]
    (seesaw-core/config! next-step-button :text button-text :visible? true)))

(defn confirm-action [trade e]
  (execute-next-step-call trade e confirm-trade-call/call))

(defn attach-confirm-action [parent-component trade]
  (attach-next-step-action parent-component trade confirm-action (terms/accept-trade)))

(defn payment-sent-action [trade e]
  (execute-next-step-call trade e payment-sent-call/call))

(defn attach-payment-sent-action [parent-component trade]
  (attach-next-step-action parent-component trade payment-sent-action (terms/payment-sent)))

(defn payment-received-action [trade e]
  (execute-next-step-call trade e payment-received-call/call))

(defn attach-payment-received-action [parent-component trade]
  (attach-next-step-action parent-component trade payment-received-action (terms/confirm-payment-received)))

(defn find-and-attach-next-step-action [parent-component trade]
  (let [next-step-key (trade-model/next-step-key trade)]
    (cond
      (= next-step-key trade-model/needs-to-be-confirmed-key) (attach-confirm-action parent-component trade)
      (= next-step-key trade-model/send-has-key) (attach-payment-sent-action parent-component trade)
      (= next-step-key trade-model/send-wants-receipt-key) (attach-payment-received-action parent-component trade))
    parent-component))

(defn attach [parent-component trade]
  (find-and-attach-next-step-action (attach-cancel-action parent-component) trade))

(defn show [trade]
  (let [trade (trade-model/as-view-trade (:id trade))]
    (controller-utils/show (attach (load-data (view-view/create) trade) trade))))