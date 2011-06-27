(ns darkexchange.model.peer
  (:require [clojure.contrib.logging :as logging]
            [clojure.java.io :as java-io]
            [clojure.tools.loading-utils :as loading-utils]
            [clj-record.boot :as clj-record-boot]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.calls.notify :as notify-call]
            [darkexchange.model.client :as client]
            [darkexchange.model.i2p-server :as i2p-server]
            [darkexchange.model.property :as property])
  (:use darkexchange.model.base)
  (:import [java.sql Clob]
           [java.text SimpleDateFormat]
           [java.util Date]))

(def peers-file-name "peers.txt")

(def peer-update-listeners (atom []))

(def peer-delete-listeners (atom []))

(defn add-peer-update-listener [listener]
  (swap! peer-update-listeners conj listener))

(defn add-peer-delete-listener [listener]
  (swap! peer-delete-listeners conj listener))

(defn peer-update [peer]
  (doseq [listener @peer-update-listeners]
    (listener peer)))

(defn peer-delete [peer]
  (doseq [listener @peer-delete-listeners]
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
              (:after-load peer-clean-up)
              (:after-destroy peer-delete)))

(defn all-peers []
  (find-records [true]))

(defn local? [peer]
  (as-boolean (:local peer)))

(defn all-foreign-peers []
  (find-by-sql ["SELECT * FROM peers WHERE local IS NULL OR local = 0"]))

(defn as-row [peer]
  (into-array Object [(:id peer) (:destination peer) (:created_at peer) (:updated_at peer)]))

(defn all-table-row-peers []
  (into-array Object (map as-row (all-peers))))

(defn add-destination [destination]
  (when destination
    (insert { :destination (i2p-server/as-destination-str destination) :created_at (new Date) :updated_at (new Date) })))

(defn add-local-destination [destination]
  (when destination
    (insert { :destination (i2p-server/as-destination-str destination) :created_at (new Date) :updated_at (new Date)
              :notified 1 :local 1 })))

(defn find-peer [destination]
  (find-record { :destination (i2p-server/as-destination-str destination) }))

(defn destination-for [peer]
  (:destination (find-record peer)))

(defn remove-destination [destination]
  (when-let [peer (find-peer destination)]
    (destroy-record peer)))

(defn update-destination [destination]
  (if-let [peer (find-peer destination)]
    (update { :id (:id peer) :updated_at (new Date) :notified true })
    (when-let [peer-id (add-destination destination)]
      (update { :id peer-id :notified true }))))

(defn notify-destination [destination]
  (if (notify-call/call destination)
    (update (merge (find-peer (i2p-server/as-destination-str destination)) { :notified true :updated_at (new Date) }))
    (logging/info (str "Destination " destination " is not online."))))

(defn get-peers-from [destination]
  (when destination
    (client/send-message destination action-keys/get-peers-action-key { :type :all })))

(defn last-updated-peer []
  (first (find-by-sql ["SELECT * FROM peers WHERE local IS NULL OR local = 0 ORDER BY updated_at DESC LIMIT 1"])))

(defn peers-text-reader []
  (java-io/reader (java-io/resource peers-file-name)))

(defn peers-text-lines []
  (filter #(< 0 (count (.trim %1))) (line-seq (peers-text-reader))))

(defn default-destinations []
  (peers-text-lines))

(defn destination-online? [destination]
  (when (i2p-server/destination-online? destination)
    destination))

(defn find-online-destination []
  (some destination-online? (cons (:destination (last-updated-peer)) (default-destinations))))

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
  (as-boolean (:notified peer)))

(defn notify-peer-if-necessary [destination]
  (when-let [peer (find-peer destination)]
    (when-not (notified? peer)
      (notify-destination destination))))

(defn add-destinations [destinations]
  (doseq [destination destinations]
    (add-destination-if-missing destination))
  (notify-all-peers))

(defn all-network-destinations []
  (let [online-destination (find-online-destination)]
    (filter #(not (= (i2p-server/base-64-destination) %)) ; Do not add yourself to the peer table.
      (cons online-destination (:data (get-peers-from online-destination))))))

(defn download-peers-background []
  (when-not (property/test-peers-downloaded?)
    (try
      (let [destinations (all-network-destinations)]
        (if (and destinations (not-empty destinations))
          (add-destinations destinations)
          (property/reset-peers-downloaded?)))
      (catch Exception e
        (logging/error "An exception occured while downloading the peers.")
        (property/reset-peers-downloaded?)
        (throw e)))))

(defn download-peers []
  (.start (Thread. download-peers-background)))

(defn send-messages [action data call-back]
  (client/send-messages (map :destination (all-foreign-peers)) action data call-back))