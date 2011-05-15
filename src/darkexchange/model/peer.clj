(ns darkexchange.model.peer
  (:require [clojure.contrib.logging :as logging]
            [clj-record.boot :as clj-record-boot])
  (:use darkexchange.model.base))

(clj-record.core/init-model)

(defn all-peers []
  (find-records [true]))

(defn as-row [peer]
  (into-array Object [(:id peer) (:destination peer) (:created-at peer) (:updated-at peer)]))

(defn all-table-row-peers []
  (into-array Object (map as-row (all-peers))))