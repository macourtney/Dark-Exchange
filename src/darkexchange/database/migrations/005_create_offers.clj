(ns darkexchange.database.migrations.005-create-offers
  (:use darkexchange.database.util))

(defn
#^{:doc "Creates the offers table."}
  up []
  (create-table :offers
    (id)
    (date-time :created_at)
    (belongs-to :acceptor) ; The id of the identity of the acceptor. Nil until the offer is accepted.
    (belongs-to :user)
    (integer :accept_confirm)))
  
(defn
#^{:doc "Deletes the offers table."}
  down []
  (drop-table :offers))