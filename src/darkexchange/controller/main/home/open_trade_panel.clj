(ns darkexchange.controller.main.home.open-trade-panel
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as action-utils]
            [darkexchange.controller.trade.view :as view-trade]
            [darkexchange.controller.widgets.utils :as widgets-utils]
            [darkexchange.model.trade :as trade-model]
            [darkexchange.model.user :as user-model]
            [darkexchange.view.main.home.open-trade-panel :as open-trade-panel]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table])
  (:import [java.awt Color Font]
           [javax.swing JLabel]
           [javax.swing.table TableCellRenderer]))

(def requires-action-color (Color/YELLOW))
(def unseen-message-color (Color/CYAN))

(defn find-open-trade-table [main-frame]
  (seesaw-core/select main-frame ["#open-trade-table"]))

(defn find-view-open-trade-button [main-frame]
  (seesaw-core/select main-frame ["#view-open-trade-button"]))

(defn load-open-trade-table [main-frame]
  (when-let [open-trades (trade-model/table-open-trades)]
    (seesaw-core/config! (find-open-trade-table main-frame)
      :model [:columns open-trade-panel/open-trade-table-columns
              :rows open-trades]))
  main-frame)

(defn view-open-trade-if-enabled [main-frame]
  (widgets-utils/do-click-if-enabled (find-view-open-trade-button main-frame)))

(defn attach-view-open-trade-table-action [main-frame]
  (widgets-utils/add-table-action (find-open-trade-table main-frame)
    #(view-open-trade-if-enabled main-frame))
  main-frame)

(defn attach-view-open-trade-enable-listener [main-frame]
  (widgets-utils/single-select-table-button (find-view-open-trade-button main-frame) (find-open-trade-table main-frame))
  main-frame)

(defn view-open-trade-listener [main-frame e]
  (let [open-trade-table (find-open-trade-table main-frame)]
    (view-trade/show main-frame (seesaw-table/value-at open-trade-table (seesaw-core/selection open-trade-table)))))

(defn attach-view-open-trade-listener [main-frame]
  (action-utils/attach-frame-listener main-frame "#view-open-trade-button" view-open-trade-listener))

(defn attach-open-trade-actions [main-frame]
  (attach-view-open-trade-table-action
    (attach-view-open-trade-enable-listener
      (attach-view-open-trade-listener main-frame))))

(defn new-trade-listener [main-frame trade]
  (when (and (= (:id (user-model/current-user)) (:user_id trade)) (trade-model/open-trade? trade))
    (seesaw-table/insert-at! (find-open-trade-table main-frame) 0 (trade-model/convert-to-table-trade trade))))

(defn attach-new-trade-listener [main-frame]
  (trade-model/add-trade-add-listener #(seesaw-core/invoke-later (new-trade-listener main-frame %)))
  main-frame)

(defn find-trade-index [open-trade-table trade]
  (some #(when (= (:id trade) (:id (second %1))) (first %1))
    (map #(list %1 (seesaw-table/value-at open-trade-table %1)) (range (seesaw-table/row-count open-trade-table)))))

(defn replace-trade-at [open-trade-table trade trade-index]
  (seesaw-table/remove-at! open-trade-table trade-index)
  (when (trade-model/open-trade? trade)
    (seesaw-table/insert-at! open-trade-table trade-index (trade-model/convert-to-table-trade trade))))

(defn trade-update-listener [main-frame trade]
  (seesaw-core/invoke-later
    (let [open-trade-table (find-open-trade-table main-frame)]
      (when-let [trade-index (find-trade-index open-trade-table trade)]
        (replace-trade-at open-trade-table trade trade-index)))))

(defn attach-trade-update-listener [main-frame]
  (trade-model/add-update-trade-listener (fn [trade] (trade-update-listener main-frame trade)))
  main-frame)

(defn trade-delete-listener [main-frame trade]
  (seesaw-core/invoke-later
    (let [open-trade-table (find-open-trade-table main-frame)]
      (when-let [trade-index (find-trade-index open-trade-table trade)]
        (seesaw-table/remove-at! open-trade-table trade-index)))))

(defn attach-trade-delete-listener [main-frame]
  (trade-model/add-delete-trade-listener (fn [trade] (trade-delete-listener main-frame trade)))
  main-frame)

(defn set-requires-action-background [render-component table row]
  (let [row-map (seesaw-table/value-at table row)
        trade (:original-trade row-map)]
    (when (trade-model/requires-action? trade)
      (.setBackground render-component requires-action-color)
      true)))

(defn set-unseen-message-background [render-component table row]
  (let [row-map (seesaw-table/value-at table row)]
    (when (:unseen-message? row-map)
      (.setBackground render-component unseen-message-color))))

(defn set-background [render-component table is-selected row]
  (if is-selected
    (.setBackground render-component (.getSelectionBackground table))
    (when-not (set-requires-action-background render-component table row)
      (set-unseen-message-background render-component table row))))

(defn attach-trade-table-cell-renderer [main-frame]
  (let [open-trade-table (find-open-trade-table main-frame)
        original-renderer (.getDefaultRenderer open-trade-table Object)]
    (.setDefaultRenderer open-trade-table Object
      (reify TableCellRenderer
        (getTableCellRendererComponent [this table value is-selected has-focus row column]
          (let [render-component (.getTableCellRendererComponent original-renderer table value is-selected has-focus row column)]
            (set-background render-component table is-selected row)
            render-component)))))
  main-frame)

(defn attach-trade-listeners [main-frame]
  (attach-trade-table-cell-renderer
    (attach-open-trade-actions
      (attach-new-trade-listener
        (attach-trade-update-listener
          (attach-trade-delete-listener main-frame))))))

(defn load-data [main-frame]
  (load-open-trade-table main-frame))

(defn attach [main-frame]
  (attach-trade-listeners main-frame))