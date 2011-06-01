(ns darkexchange.database.migrations.005-create-offers
  (:use darkexchange.database.util))

(defn
#^{:doc "Creates the offers table."}
  up []
  (create-table :offers
    (id)
    (date-time :created_at)
    (belongs-to :identity) ; The id of the identity of the acceptor. Nil until the offer is accepted.
    (belongs-to :user)
    (integer :accept_confirm)
    (integer :has_amount)
    (string :has_currency) ; Currency code.
    (string :has_payment_type) ; Payment type code.
    (integer :wants_amount)
    (string :wants_currency) ; Currency code.
    (string :wants_payment_type))) ; Payment type code.
  
(defn
#^{:doc "Deletes the offers table."}
  down []
  (drop-table :offers))