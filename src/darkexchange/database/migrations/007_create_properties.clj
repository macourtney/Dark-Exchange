(ns darkexchange.database.migrations.007-create-properties
  (:use darkexchange.database.util))

(defn
#^{:doc "Migrates the database up to version 7."}
  up []
  (create-table :properties
    (id)
    (string :name)
    (string :value)))
  
(defn
#^{:doc "Migrates the database down from version 7."}
  down []
  (drop-table :properties))