(ns darkexchange.database.migrations.006-create-trades
  (:use darkexchange.database.util))

(defn
#^{:doc "Creates the trades table in the database."}
  up []
  (create-table :trades
    (id)
    (date-time :created_at)
    (belongs-to :user)
    (belongs-to :offer)
    (integer :foreign_trade_id) ; The id of the matching trade on the other system.
    (integer :accept_confirm)
    (integer :wants_first) ; Set to true if the wants currency should be sent first.
    (integer :wants_sent) ; Set to true when the wants is sent.
    (integer :wants_received) ; Set to true when the wants is received.
    (integer :has_sent) ; Set to true when the has is sent
    (integer :has_received) ; Set to true when the has is received.
    (integer :closed))) ; Set to true when all transfers are sent and received.
  
(defn
#^{:doc "Drops the trades table in the database."}
  down []
  (drop-table :trades))