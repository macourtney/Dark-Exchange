(ns darkexchange.database.migrations.003-create-has-offers
  (:use darkexchange.database.util))

(defn
#^{:doc "Migrates the database up to version 3."}
  up []
  (create-table :has-offers
    (id)
    (integer :amount)
    (string :currency) ; Currency code.
    (string :payment-type) ; Payment type code.
    (belongs-to :offer)))
  
(defn
#^{:doc "Migrates the database down from version 3."}
  down []
  (drop-table :has-offers))