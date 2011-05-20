(ns darkexchange.model.peer
  (:require [clojure.contrib.logging :as logging]
            [clj-record.boot :as clj-record-boot]
            [darkexchange.model.client :as client]
            [darkexchange.model.i2p-server :as i2p-server])
  (:use darkexchange.model.base)
  (:import [java.sql Clob]
           [java.text SimpleDateFormat]
           [java.util Date]))

(def notify-action-key :notify)
(def get-peers-action-key :get-peers)

(def peer-update-listeners (atom []))

(defn add-peer-update-listener [peer-update-listener]
  (swap! peer-update-listeners conj peer-update-listener))

(defn peer-update [peer]
  (doseq [listener @peer-update-listeners]
    (listener peer)))

(defn load-clob [clob]
  (let [clob-str (.toString clob)]
    (.substring clob-str (inc (.indexOf clob-str "'")) (dec (.length clob-str)))))

(defn peer-clean-up [peer]
  (if-let [destination-clob (:destination peer)]
    (if (instance? Clob destination-clob)
      (assoc peer :destination (load-clob destination-clob))
      peer)
    peer))

(clj-record.core/init-model
  (:callbacks (:after-update peer-update)
              (:after-insert peer-update)
              (:after-load peer-clean-up)))

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
    (update { :id (:id peer) :updated_at (new Date) })
    (add-destination destination)))

(defn notify-action [data]
  (update-destination (:destination data))
  "ok")

(defn notify-destination [destination]
  (client/send-message destination notify-action-key { :destination (client/base-64-destination) }))

(defn get-peers-from [destination]
  (client/send-message destination get-peers-action-key { :type :all }))

(defn get-peers-action [data]
  (map :destination (all-peers)))