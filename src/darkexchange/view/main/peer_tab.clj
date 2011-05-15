(ns darkexchange.view.main.peer-tab
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.terms :as terms]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing.table DefaultTableColumnModel TableColumn]))

(def tab-name (terms/peer))

(def peer-table-headers [(terms/id) (terms/destination) (terms/created-on) (terms/last-updated-at)])

(defn create-destination-text-area []
  (let [text-area (seesaw-core/text
                    :id :destination-text
                    :multi-line? true
                    :editable? false
                    :preferred-size [400 :by 60])]
    (.setLineWrap text-area true)
    text-area))

(defn create-destination-text []
  (seesaw-core/scrollable (create-destination-text-area)))

(defn create-destination-panel []
  (seesaw-core/vertical-panel 
    :id :north-panel 
    :items [(terms/destination-address) [:fill-v 3] (create-destination-text)]))

(defn create-peer-list-header-panel []
  (seesaw-core/border-panel
    :west (terms/peers)))

(defn create-table-column [model-index header-text]
  (let [table-column (TableColumn. model-index)]
    (.setHeaderValue table-column header-text)
    table-column))

(defn create-peer-list-table []
  (seesaw-core/scrollable
    (seesaw-core/table :id :peer-table :preferred-size [600 :by 300])))

(defn create-peer-list-panel []
  (seesaw-core/vertical-panel
    :items [(create-peer-list-header-panel) [:fill-v 3] (create-peer-list-table)]))

(defn create []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :north (create-destination-panel)
      :center (create-peer-list-panel)))