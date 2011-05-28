(ns darkexchange.database.migrations.001-create-properties
  (:use darkexchange.database.util))

(defn
#^{:doc "Creates the properties table."}
  up []
  (create-table :properties
    (id)
    (string :name)
    (string :value)))
  
(defn
#^{:doc "Drops the properties table."}
  down []
  (drop-table :properties))