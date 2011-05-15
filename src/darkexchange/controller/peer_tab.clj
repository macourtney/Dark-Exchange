(ns darkexchange.controller.peer-tab
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.i2p-server :as i2p-server]
            [darkexchange.model.peer :as peers-model]
            [darkexchange.view.main.peer-tab :as peer-tab-view]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing.table DefaultTableModel]))

(defn find-destination-text [main-frame]
  (seesaw-core/select main-frame ["#destination-text"]))

(defn set-destination-text [main-frame destination]
  (.setText (find-destination-text main-frame) (.toBase64 destination)))

(defn create-destination-listener [main-frame]
  (fn [destination]
    (set-destination-text main-frame destination)))

(defn load-destination [main-frame]
  (let [destination (i2p-server/current-destination)]
    (if destination
      (set-destination-text main-frame destination)
      (i2p-server/add-destination-listener (create-destination-listener main-frame)))))

(defn find-peer-table [main-frame]
  (seesaw-core/select main-frame ["#peer-table"]))

(defn add-rows [table-model table-rows]
  (doseq [row table-rows]
    (.addRow table-model row)))

(defn create-peer-table-model []
  (let [table-model (new DefaultTableModel)]
    (.setColumnIdentifiers table-model (into-array Object peer-tab-view/peer-table-headers))
    (add-rows table-model (peers-model/all-table-row-peers))
    table-model))

(defn load-peer-table [main-frame]
  (let [peer-table (find-peer-table main-frame)]
    (.setModel peer-table (create-peer-table-model))))

(defn attach [main-frame]
  (load-destination main-frame)
  (load-peer-table main-frame))