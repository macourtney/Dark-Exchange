(ns darkexchange.database.migrations.007-create-trade-messages
  (:use darkexchange.database.util))

(defn
#^{:doc "Creates the trade messages table in the database."}
  up []
  (create-table :trade-messages
    (id)
    (date-time :created_at)
    (belongs-to :trade)
    (belongs-to :identity) ; Who the message is froms
    (integer :foreign_message_id)
    (text :body)))
  
(defn
#^{:doc "Drops the trade messages table in the database."}
  down []
  (drop-table :trade-messages))