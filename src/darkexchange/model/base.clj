(ns darkexchange.model.base
  (:require [clojure.string :as string]
            [darkexchange.database.util :as database-util])
  (:import [java.sql Clob]))

(def db (deref database-util/db))

(defn clob-string [clob]
  (when clob
    (let [clob-stream (.getCharacterStream clob)]
      (string/join "\n" (take-while identity (repeatedly #(.readLine clob-stream)))))))

(defn load-clob [clob]
  (clob-string clob))

(defn get-clob [record clob-key]
  (when-let [clob (clob-key record)]
    (when (instance? Clob clob)
      clob)))

(defn clean-clob-key [record clob-key]
  (if-let [clob (get-clob record clob-key)]
    (assoc record clob-key (load-clob clob))
    record))

(defn as-boolean [value]
  (and value (not (= value 0))))

(defn remove-listener [listeners listener-to-remove]
  (remove #(= listener-to-remove %) listeners))