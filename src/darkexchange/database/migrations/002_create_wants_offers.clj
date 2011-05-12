(ns darkexchange.database.migrations.002-create-wants-offers
  (:use darkexchange.database.util))

(defn
#^{:doc "Migrates the database up to version 2."}
  up []
  (create-table :wants-offers
    (id)
    (integer :amount)
    (integer :currency)
    (belongs-to :offer)))
  
(defn
#^{:doc "Migrates the database down from version 2."}
  down []
  (drop-table :wants-offers))