(ns darkexchange.controller.trade.view
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.controller.trade.message.view :as message-view]
            [darkexchange.controller.trade.message.send :as send-message]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.controller.widgets.utils :as widgets-utils]
            [darkexchange.model.calls.confirm-trade :as confirm-trade-call]
            [darkexchange.model.calls.payment-received :as payment-received-call]
            [darkexchange.model.calls.payment-sent :as payment-sent-call]
            [darkexchange.model.calls.reject-trade :as reject-trade-call]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.model.terms :as terms]
            [darkexchange.model.trade :as trade-model]
            [darkexchange.model.trade-message :as trade-message-model]
            [darkexchange.view.trade.view :as view-view]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table])
  (:import [java.awt Color]
           [java.awt.event WindowListener]
           [javax.swing.table TableCellRenderer]))

(def my-message-color Color/LIGHT_GRAY)
(def unviewed-message-color Color/CYAN)

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
  (load-data-label (trade-model/trade-partner-text trade)
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

(defn find-trade-messages-table [parent-component]
  (seesaw-core/select parent-component ["#trade-messages-table"]))

(defn load-messages [trade parent-component]
  (when-let [trade-messages (trade-model/table-trade-messages trade)]
    (seesaw-core/config! (find-trade-messages-table parent-component)
      :model [:columns view-view/trade-messages-table-columns
              :rows trade-messages])
    (future (trade-message-model/mark-as-seen trade-messages)))
  parent-component)

(defn load-data [parent-component trade]
  (load-messages trade
    (load-waiting-for trade
      (load-offer-data trade
        (load-partner-data trade
            (load-initiated-at trade
              (load-trade-id trade parent-component)))))))

(defn attach-cancel-action [parent-component]
  (actions-utils/attach-window-close-listener parent-component "#cancel-button"))

(defn execute-next-step-call [trade e call]
  (future
    (call trade))
  (actions-utils/close-window (seesaw-core/to-frame e)))

(defn attach-next-step-action [parent-component trade action button-text]
  (actions-utils/attach-listener parent-component "#next-step-button" #(action trade %))
  (let [next-step-button (controller-utils/find-component parent-component "#next-step-button")]
    (seesaw-core/config! next-step-button :text button-text :visible? true)))

(defn confirm-action [trade e]
  (execute-next-step-call trade e confirm-trade-call/call))

(defn attach-confirm-action [parent-component trade]
  (attach-next-step-action parent-component trade confirm-action (terms/accept-trade))
  (seesaw-core/config! (controller-utils/find-component parent-component "#reject-button") :visible? true))

(defn payment-sent-action [trade e]
  (execute-next-step-call trade e payment-sent-call/call))

(defn attach-payment-sent-action [parent-component trade]
  (attach-next-step-action parent-component trade payment-sent-action (terms/payment-sent)))

(defn payment-received-action [trade e]
  (execute-next-step-call trade e payment-received-call/call))

(defn attach-payment-received-action [parent-component trade]
  (attach-next-step-action parent-component trade payment-received-action (terms/confirm-payment-received)))

(defn reject-trade-action [trade e]
  (future
    (reject-trade-call/call trade))
  (actions-utils/close-window (seesaw-core/to-frame e)))

(defn attach-reject-trade-action [parent-component trade]
  (actions-utils/attach-listener parent-component "#reject-button" #(reject-trade-action trade %)))

(defn close-action [trade e]
  (future
    (trade-model/close trade)
    (seesaw-core/invoke-later (actions-utils/close-window (seesaw-core/to-frame e)))))

(defn attach-close-action [parent-component trade]
  (attach-next-step-action parent-component trade close-action (terms/close-trade)))

(defn find-and-attach-next-step-action [trade parent-component]
  (attach-reject-trade-action parent-component trade)
  (let [next-step-key (trade-model/next-step-key trade)]
    (cond
      (= next-step-key trade-model/rejected-key) (attach-close-action parent-component trade)
      (= next-step-key trade-model/needs-to-be-confirmed-key) (attach-confirm-action parent-component trade)
      (= next-step-key trade-model/send-has-key) (attach-payment-sent-action parent-component trade)
      (= next-step-key trade-model/send-wants-receipt-key) (attach-payment-received-action parent-component trade))
    parent-component))

(defn send-message-action [trade e]
  (send-message/show (seesaw-core/to-frame e) trade))

(defn attach-send-message-action [trade parent-component]
  (actions-utils/attach-listener parent-component "#send-message-button" #(send-message-action trade %)))

(defn new-message-listener [trade-message trade parent-component]
  (when (= (:trade_id trade-message) (:id trade))
    (let [trade-messages-table (find-trade-messages-table parent-component)]
      (seesaw-table/insert-at! trade-messages-table (seesaw-table/row-count trade-messages-table)
        (trade-message-model/as-table-trade-message trade-message)))))

(defn add-window-listener [parent-component listener]
  (.addWindowListener (seesaw-core/to-frame parent-component) listener))

(defn create-on-window-closed-listener [listener]
  (reify WindowListener
    (windowActivated [_ _])
    (windowClosed [_ _] (listener))
    (windowClosing [_ _])
    (windowDeactivated [_ _])
    (windowDeiconified [_ _])
    (windowIconified [_ _])
    (windowOpened [_ _])))

(defn remove-add-message-listener-window-listener [trade-message-listener]
  (create-on-window-closed-listener #(trade-message-model/remove-message-add-listener trade-message-listener)))

(defn attach-new-message-listener [trade parent-component]
  (let [trade-message-listener #(new-message-listener % trade parent-component)]
    (trade-message-model/add-message-add-listener trade-message-listener)
    (add-window-listener parent-component (remove-add-message-listener-window-listener trade-message-listener)))
  parent-component)

(defn update-message-listener [trade-message trade parent-component]
  (when-let [trade-message (trade-message-model/find-record { :id (:id trade-message) })]
    (when (= (:trade_id trade-message) (:id trade))
      (let [trade-messages-table (find-trade-messages-table parent-component)]
        (controller-utils/update-record-in-table trade-messages-table
          (trade-message-model/as-table-trade-message trade-message))))))

(defn remove-update-message-listener-window-listener [trade-message-listener]
  (create-on-window-closed-listener #(trade-message-model/remove-message-update-listener trade-message-listener)))

(defn attach-update-message-listener [trade parent-component]
  (let [trade-message-listener #(update-message-listener % trade parent-component)]
    (trade-message-model/add-message-update-listener trade-message-listener)
    (add-window-listener parent-component (remove-update-message-listener-window-listener trade-message-listener)))
  parent-component)

(defn view-message-action [parent-component e]
  (let [trade-messages-table (find-trade-messages-table parent-component)]
    (when-let [selected-row (seesaw-core/selection trade-messages-table)]
      (message-view/show parent-component (:id (seesaw-table/value-at trade-messages-table selected-row))))))

(defn attach-view-message-action [parent-component]
  (actions-utils/attach-frame-listener parent-component "#view-message-button" view-message-action))

(defn find-view-message-button [parent-component]
  (seesaw-core/select parent-component ["#view-message-button"]))

(defn view-message-if-enabled [parent-component]
  (widgets-utils/do-click-if-enabled (find-view-message-button parent-component)))

(defn attach-view-message-table-action [parent-component]
  (widgets-utils/add-table-action (find-trade-messages-table parent-component)
    #(view-message-if-enabled parent-component))
  parent-component)

(defn attach-view-message-enable-listener [parent-component]
  (widgets-utils/single-select-table-button (find-view-message-button parent-component)
    (find-trade-messages-table parent-component))
  parent-component)

(defn set-my-message-background [render-component table row]
  (let [row-map (seesaw-table/value-at table row)]
    (when (identity-model/is-user-identity? (:identity (:original-message row-map)))
      (.setBackground render-component my-message-color)
      true)))

(defn set-unviewed-message-background [render-component table row]
  (let [row-map (seesaw-table/value-at table row)]
    (when-not (trade-message-model/viewed? (:original-message row-map))
      (.setBackground render-component unviewed-message-color)
      true)))

(defn set-background [render-component table is-selected row]
  (if is-selected
    (.setBackground render-component (.getSelectionBackground table))
    (when-not (or (set-my-message-background render-component table row)
                (set-unviewed-message-background render-component table row))
      (.setBackground render-component Color/WHITE))))

(defn attach-message-table-cell-renderer [parent-component]
  (let [trade-messages-table (find-trade-messages-table parent-component)
        original-renderer (.getDefaultRenderer trade-messages-table Object)]
    (.setDefaultRenderer trade-messages-table Object
      (reify TableCellRenderer
        (getTableCellRendererComponent [this table value is-selected has-focus row column]
          (let [render-component (.getTableCellRendererComponent original-renderer table value is-selected has-focus row column)]
            (set-background render-component table is-selected row)
            render-component)))))
  parent-component)

(defn attach-message-actions [trade parent-component]
  (attach-message-table-cell-renderer
    (attach-view-message-table-action
      (attach-view-message-enable-listener
        (attach-view-message-action
          (attach-new-message-listener trade
            (attach-update-message-listener trade
              (attach-send-message-action trade parent-component))))))))

(defn attach [parent-component trade]
  (attach-message-actions trade
    (find-and-attach-next-step-action trade
      (attach-cancel-action parent-component))))

(defn show [main-frame trade]
  (let [view-trade-frame (view-view/create main-frame)]
    (controller-utils/disable-widget view-trade-frame)
    (controller-utils/show view-trade-frame)
    (future
      (let [trade (trade-model/as-view-trade (:id trade))]
        (seesaw-core/invoke-later
          (attach (load-data view-trade-frame trade) trade)
          (controller-utils/enable-widget view-trade-frame))))))