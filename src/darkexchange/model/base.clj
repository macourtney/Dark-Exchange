(ns darkexchange.model.base
  (:require [darkexchange.database.util :as database-util]))

(def db (deref database-util/db))

(defn load-clob [clob]
  (let [clob-str (.toString clob)]
    (.substring clob-str (inc (.indexOf clob-str "'")) (dec (.length clob-str)))))