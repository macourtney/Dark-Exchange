(ns darkexchange.controller.peer-tab
  (:require [clojure.contrib.logging :as logging]
            [clojure.string :as clj-string]
            [darkexchange.controller.add-destination.add-destination :as add-destination]
            [darkexchange.model.client :as client]
            [darkexchange.model.i2p-server :as i2p-server]
            [darkexchange.model.peer :as peers-model]
            [darkexchange.model.server :as server]
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

(defn reload-table-data [main-frame]
  (seesaw-core/config! (find-peer-table main-frame)
    :model [:columns peer-tab-view/peer-table-columns
            :rows (peers-model/all-peers)]))

(defn load-peer-table [main-frame]
  (reload-table-data main-frame))

(defn find-add-button [main-frame]
  (seesaw-core/select main-frame ["#add-button"]))

(defn attach-listener-to-add-button [main-frame]
  (seesaw-core/listen (find-add-button main-frame) :action
    (fn [e]
      (add-destination/show #(reload-table-data main-frame)))))

(defn find-test-button [main-frame]
  (seesaw-core/select main-frame ["#test-button"]))

(defn attach-listener-to-test-button [main-frame]
  (seesaw-core/listen (find-test-button main-frame) :action
    (fn [e]
      (if-let [current-destination (i2p-server/current-destination)]
        (logging/debug (str "Notification status: \n" (clj-string/join "\n" (:data (peers-model/get-peers-from current-destination)))))
        (logging/debug "Current destination not set yet.")))))

(defn create-peer-listener [main-frame]
  (fn [_] (reload-table-data main-frame)))

(defn attach-peer-listener [main-frame]
  (peers-model/add-peer-update-listener (create-peer-listener main-frame)))

(defn attach [main-frame]
  (load-destination main-frame)
  (load-peer-table main-frame)
  (attach-listener-to-add-button main-frame)
  (attach-listener-to-test-button main-frame)
  (attach-peer-listener main-frame))
