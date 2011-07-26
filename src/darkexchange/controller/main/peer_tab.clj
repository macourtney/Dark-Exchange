(ns darkexchange.controller.main.peer-tab
  (:require [clojure.contrib.logging :as logging]
            [clojure.string :as clj-string]
            [darkexchange.controller.actions.utils :as action-utils]
            [darkexchange.controller.add-destination.add-destination :as add-destination]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.model.client :as client]
            [darkexchange.model.i2p-server :as i2p-server]
            [darkexchange.model.peer :as peers-model]
            [darkexchange.model.property :as property]
            [darkexchange.model.server :as server]
            [darkexchange.model.terms :as terms]
            [darkexchange.view.main.peer-tab :as peer-tab-view]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table])
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
      (i2p-server/add-destination-listener (create-destination-listener main-frame))))
  main-frame)

(defn find-peer-table [main-frame]
  (seesaw-core/select main-frame ["#peer-table"]))

(defn convert-to-table-peer [peer]
  (when peer
    (assoc peer :notified (when (peers-model/notified? peer) (terms/yes)))))

(defn reload-table-data [main-frame]
  (when-let [peers (map convert-to-table-peer (peers-model/all-peers))]
    (seesaw-core/config! (find-peer-table main-frame)
      :model [:columns peer-tab-view/peer-table-columns
              :rows peers])))

(defn update-peer-id-table [main-frame peer]
  (controller-utils/update-record-in-table (find-peer-table main-frame)
    (convert-to-table-peer (peers-model/get-record (:id peer)))))

(defn delete-peer-from-table [main-frame peer]
  (controller-utils/delete-record-from-table (find-peer-table main-frame) (:id peer)))

(defn load-peer-table [main-frame]
  (reload-table-data main-frame)
  main-frame)

(defn find-add-button [main-frame]
  (seesaw-core/select main-frame ["#add-button"]))

(defn attach-listener-to-add-button [main-frame]
  (action-utils/attach-listener main-frame "#add-button" 
    (fn [e] (add-destination/show main-frame #(reload-table-data main-frame)))))

(defn attach-peer-listener [main-frame]
  (peers-model/add-peer-update-listener (fn [peer] (seesaw-core/invoke-later (update-peer-id-table main-frame peer))))
  (peers-model/add-peer-delete-listener (fn [peer] (seesaw-core/invoke-later (delete-peer-from-table main-frame peer))))
  main-frame)

(defn load-data [main-frame]
  (load-peer-table (load-destination main-frame)))

(defn attach [main-frame]
  (attach-peer-listener (attach-listener-to-add-button main-frame)))

(defn init [main-frame]
  (attach (load-data main-frame)))
