(ns darkexchange.database.migrations.006-create-has-offers
  (:use darkexchange.database.util))

(defn
#^{:doc "Creates the has-offers table in the database."}
  up []
  (create-table :has-offers
    (id)
    (integer :amount)
    (string :currency) ; Currency code.
    (string :payment-type) ; Payment type code.
    (belongs-to :offer)))
  
(defn
#^{:doc "Drops the has-offers table in the database."}
  down []
  (drop-table :has-offers))