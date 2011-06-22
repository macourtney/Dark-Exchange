(ns darkexchange.controller.widgets.utils
  (:require [seesaw.core :as seesaw-core])
  (:import [javax.swing.event ListSelectionListener]
           [java.awt.event MouseEvent MouseListener]))

(defn load-combobox [combobox data]
  (seesaw-core/config! combobox :model data))

(defn find-widget [parent-component widget-selector]
  (seesaw-core/select parent-component widget-selector))

(defn button-enabled-listener [button enable-fn]
  (.setEnabled button (enable-fn)))

(defn create-list-selection-listener [selection-listener-fn]
  (reify ListSelectionListener
    (valueChanged [this e] (selection-listener-fn e))))

(defn add-table-selection-listener [table selection-listener-fn]
  (.addListSelectionListener (.getSelectionModel table) (create-list-selection-listener selection-listener-fn)))

(defn single-select-table-button [button table]
  (add-table-selection-listener table (fn [_] (button-enabled-listener button #(> (.getSelectedRow table) -1)))))

(defn create-table-action-mouse-listener [action-fn]
  (reify MouseListener
    (mouseClicked [this e]
      (if (and (.isEnabled (.getComponent e)) (= MouseEvent/BUTTON1 (.getButton e)) (= (.getClickCount e) 2))
        (action-fn)))
    (mouseEntered [this e])
    (mouseExited [this e])
    (mousePressed [this e])
    (mouseReleased [this e])))

(defn add-table-action [table action-fn]
  (.addMouseListener table (create-table-action-mouse-listener action-fn)))

(defn do-click-if-enabled [button]
  (when (and button (.isEnabled button))
    (.doClick button)))