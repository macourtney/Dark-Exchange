(ns darkexchange.model.peer
  (:require [clojure.contrib.logging :as logging]
            [clj-record.boot :as clj-record-boot]
            [darkexchange.model.client :as client]
            [darkexchange.model.i2p-server :as i2p-server])
  (:use darkexchange.model.base)
  (:import [java.util Date]))

(def peer-update-listeners (atom []))

(defn add-peer-update-listener [peer-update-listener]
  (swap! peer-update-listeners conj peer-update-listener))

(defn peer-update [peer]
  (doseq [listener @peer-update-listeners]
    (listener peer)))

(clj-record.core/init-model
  (:callbacks (:after-update peer-update)))

(defn all-peers []
  (find-records [true]))

(defn as-row [peer]
  (into-array Object [(:id peer) (:destination peer) (:created_at peer) (:updated_at peer)]))

(defn all-table-row-peers []
  (into-array Object (map as-row (all-peers))))

(defn add-destination [destination]
  (when destination
    (insert { :destination destination :created_at (new Date) :updated_at (new Date) })))

(defn find-peer [destination]
  (find-record { :destination destination }))

(defn update-destination [destination]
  (if-let [peer (find-peer destination)]
    (update { :id peer :updated_at (new Date) })
    (add-destination destination)))

(def notify-action-key :notify)

(defn notify-action [data]
  (logging/debug (str "Notified of destination: " (:destination data)))
  (update-destination (:destination data))
  "ok")

(defn notify-destination [destination]
  (logging/debug (str "Notifying " destination))
  (client/send-message destination notify-action-key { :destination (client/base-64-destination) }))