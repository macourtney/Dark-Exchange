(ns darkexchange.model.peer
  (:require [clojure.contrib.logging :as logging]
            [clj-record.boot :as clj-record-boot]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.client :as client]
            [darkexchange.model.i2p-server :as i2p-server]
            [darkexchange.model.property :as property])
  (:use darkexchange.model.base)
  (:import [java.sql Clob]
           [java.text SimpleDateFormat]
           [java.util Date]))

(def peer-update-listeners (atom []))

(defn add-peer-update-listener [peer-update-listener]
  (swap! peer-update-listeners conj peer-update-listener))

(defn peer-update [peer]
  (doseq [listener @peer-update-listeners]
    (listener peer)))

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
    (insert { :destination (i2p-server/as-destination-str destination) :created_at (new Date) :updated_at (new Date) })))

(defn find-peer [destination]
  (find-record { :destination (i2p-server/as-destination-str destination) }))

(defn update-destination [destination]
  (if-let [peer (find-peer destination)]
    (update { :id (:id peer) :updated_at (new Date) })
    (add-destination destination)))

(defn notify-destination [destination]
  (try
    (let [response (client/send-message (i2p-server/as-destination destination) action-keys/notify-action-key
      { :destination (client/base-64-destination) })]
      (update (merge (find-peer (i2p-server/as-destination-str destination)) { :notified true :updated_at (new Date) }))
      response)
    (catch Exception e
      (logging/error (str "e: " e))
      nil)))

(defn get-peers-from [destination]
  (when destination
    (client/send-message destination action-keys/get-peers-action-key { :type :all })))

(defn last-updated-peer []
  (first (find-by-sql ["SELECT * FROM peers ORDER BY updated_at DESC LIMIT 1"])))

(defn add-destination-if-missing [destination]
  (let [peer (find-peer destination)]
    (when-not peer
      (add-destination destination))))

(defn all-unnotified-peers []
  (find-by-sql ["SELECT * FROM peers WHERE notified IS NULL"]))

(defn notify-all-peers []
  (doseq [peer (all-unnotified-peers)]
    (notify-destination (:destination peer))))

(defn notified? [peer]
  (and peer (:notified peer)))

(defn notify-peer-if-necessary [destination]
  (when-let [peer (find-peer destination)]
    (when-not (notified? peer)
      (notify-destination destination))))

(defn add-destinations [destinations]
  (doseq [destination destinations]
    (add-destination-if-missing destination))
  (notify-all-peers))

(defn download-peers-background []
  (when-not (property/test-peers-downloaded?)
    (try
      (let [destinations (:data (get-peers-from (:destination (last-updated-peer))))]
        (if (and destinations (not-empty destinations))
          (add-destinations destinations)
          (property/reset-peers-downloaded?)))
      (catch Exception e
        (logging/debug "An exception occured while downloading the peers.")
        (property/reset-peers-downloaded?)
        (throw e)))))

(defn download-peers []
  (.start (Thread. download-peers-background)))