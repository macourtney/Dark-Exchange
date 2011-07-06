(ns darkexchange.model.peer
  (:require [clojure.contrib.logging :as logging]
            [clojure.java.io :as java-io]
            [clojure.tools.loading-utils :as loading-utils]
            [clj-record.boot :as clj-record-boot]
            [config.environment :as environment]
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
(def development-peers-file-name "development-peers.txt")

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

(defn find-peer [destination]
  (find-record { :destination (i2p-server/as-destination-str destination) }))

(defn add-local-destination [destination]
  (when (and destination (not (find-peer destination)))
    (insert { :destination (i2p-server/as-destination-str destination) :created_at (new Date) :updated_at (new Date)
              :notified 1 :local 1 })))

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
    (:data (client/send-message destination action-keys/get-peers-action-key { :type :all }))))

(defn last-updated-peer []
  (first (find-by-sql ["SELECT * FROM peers WHERE local IS NULL OR local = 0 ORDER BY updated_at DESC LIMIT 1"])))

(defn peers-text-resource []
  (if (= (environment/environment-name) "development")
    development-peers-file-name
    peers-file-name))

(defn peers-text-reader []
  (java-io/reader (java-io/resource (peers-text-resource))))

(defn peers-text-lines []
  (filter #(< 0 (count (.trim %1))) (line-seq (peers-text-reader))))

(defn default-destinations []
  (shuffle (peers-text-lines)))

(defn destination-online? [destination]
  (when (i2p-server/destination-online? destination)
    destination))

(defn find-online-destination
  ([] (find-online-destination 0))
  ([count]
    (if-let [online-destination (some destination-online? (filter identity (cons (:destination (last-updated-peer)) (default-destinations))))]
      online-destination
      (when (< count 10)
        (find-online-destination (inc count))))))

(defn add-destination-if-missing [destination]
  (when (and destination (not (find-peer destination)))
    (add-destination destination)))

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
    (add-destination-if-missing destination)))

(defn load-all-peers-from [destination]
  (add-destinations (filter #(and %1 (not (= (i2p-server/base-64-destination) %1))) (get-peers-from destination))))

(defn load-all-peers []
  (when-let [online-destination (find-online-destination)]
    (add-destination-if-missing online-destination)
    (load-all-peers-from online-destination)
    online-destination))

(defn download-peers-background
  ([] (download-peers-background 0))
  ([attempt-count]
    (when-not (property/peers-downloaded?)
      (try
        (when (load-all-peers)
          (property/set-peers-downloaded? true))
        (catch Exception e
          (logging/error "An exception occured while downloading the peers.")
          (property/reset-peers-downloaded?)
          (throw e))))
    (notify-all-peers)))

(defn download-peers []
  (.start (Thread. download-peers-background)))

(defn send-messages [action data call-back]
  (client/send-messages (map :destination (all-foreign-peers)) action data call-back))