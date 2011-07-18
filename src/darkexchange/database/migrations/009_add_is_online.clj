(ns darkexchange.database.migrations.009-add-is-online
  (:require [clojure.contrib.logging :as logging])
  (:use darkexchange.database.util))

(defn describe-identities-table []
  (seq (map #(select-keys % [:field]) (describe-table :identities))))

(defn
#^{:doc "Migrates the database up to version 9."}
  up []
  (execute-commands "ALTER TABLE IDENTITIES ADD IF NOT EXISTS IS_ONLINE INT"))
  
(defn
#^{:doc "Migrates the database down from version 9."}
  down []
  (execute-commands "ALTER TABLE identities DROP COLUMN IF EXISTS is_online"))