(ns darkexchange.database.migrations.001-create-offers
  (:use darkexchange.database.util))

(defn
#^{:doc "Creates the offers table."}
  up []
  (create-table :offers
    (id)
    (date-time :created-at)
    (belongs-to :acceptor) ; The id of the identity of the acceptor. Nil until the offer is accepted.
    (integer :accept-confirm)))
  
(defn
#^{:doc "Deletes the offers table."}
  down []
  (drop-table :offers))