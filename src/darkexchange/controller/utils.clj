(ns darkexchange.controller.utils
  (:require [clojure.contrib.logging :as logging]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table])
  (:import [java.awt Color]
           [java.awt.event ItemListener]
           [javax.swing JComponent]))

(def yellow-highlight (Color. (float 1) (float 1) (float 0.6)))
(def turquoise-highlight (Color. (float 0.7) (float 1) (float 1)))
(def gray-highlight (Color. (float 0.92) (float 0.92) (float 0.92)))

(defn find-component [parent-component id]
  (seesaw-core/select parent-component [id]))

(defn show [frame]
  (if frame
    (seesaw-core/show! frame)
    (logging/warn "Show was given a nil frame.")))

(defn create-item-listener [item-listener-fn]
  (reify ItemListener
    (itemStateChanged [this e]
      (item-listener-fn e))))

(defn attach-item-listener [component item-listener-fn]
  (.addItemListener component (create-item-listener item-listener-fn))
  component)

(defn table-row-pairs [table]
  (map #(list %1 (seesaw-table/value-at table %1))
    (range (seesaw-table/row-count table))))

(defn find-table-record-pair [table record-id]
  (some #(when (= (:id (second %1)) record-id) %1)
    (table-row-pairs table)))

(defn delete-record-from-table [table record-id]
  (when-let [record-index (first (find-table-record-pair table record-id))]
    (seesaw-table/remove-at! table record-index)
    record-index))

(defn add-record-to-table 
  ([table record] (add-record-to-table table record 0))
  ([table record index]
    (seesaw-table/insert-at! table (or index 0) record)))

(defn update-record-in-table [table record]
  (add-record-to-table table record (delete-record-from-table table (:id record))))

(defn enableable-widget? [component]
  (instance? JComponent component))

(defn enableable-widgets [parent-component]
  (when parent-component
    (filter enableable-widget? (seesaw-core/select parent-component [:*]))))

(defn enable-subwidgets [parent-component enable?]
  (seesaw-core/config! (enableable-widgets parent-component) :enabled? enable?))

(defn disable-widget [parent-component]
  (enable-subwidgets parent-component false))

(defn enable-widget [parent-component]
  (enable-subwidgets parent-component true))