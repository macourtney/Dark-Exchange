(ns darkexchange.model.base
  (:require [darkexchange.database.util :as database-util])
  (:import [java.sql Clob]))

(def db (deref database-util/db))

(defn load-clob [clob]
  (let [clob-str (.toString clob)]
    (.substring clob-str (inc (.indexOf clob-str "'")) (dec (.length clob-str)))))

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