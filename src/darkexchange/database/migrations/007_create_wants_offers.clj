(ns darkexchange.database.migrations.007-create-wants-offers
  (:use darkexchange.database.util))

(defn
#^{:doc "Creates the wants_offers table."}
  up []
  (create-table :wants-offers
    (id)
    (integer :amount)
    (string :currency) ; Currency code.
    (string :payment-type) ; Payment type code.
    (belongs-to :offer)))
  
(defn
#^{:doc "Drops the wants_offers table."}
  down []
  (drop-table :wants-offers))