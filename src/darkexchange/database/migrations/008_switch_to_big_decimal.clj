(ns darkexchange.database.migrations.008-switch-to-big-decimal
  (:use darkexchange.database.util))

(defn
#^{:doc "Migrates the database up to version 8."}
  up []
  (drop-table :offers_decimal)
  (create-table :offers_decimal
    (id)
    (date-time :created_at)
    (belongs-to :user)
    (integer :foreign_offer_id) ; The id of the matching trade on the other system.
    (integer :closed)
    (decimal :has_amount)
    (string :has_currency) ; Currency code.
    (string :has_payment_type) ; Payment type code.
    (decimal :wants_amount)
    (string :wants_currency) ; Currency code.
    (string :wants_payment_type))

  (when (table-exists? :offers)
    (apply insert-into :offers_decimal (execute-query ["SELECT * FROM offers"]))
    (drop-table :offers))

  (create-table :offers
    (id)
    (date-time :created_at)
    (belongs-to :user)
    (integer :foreign_offer_id) ; The id of the matching trade on the other system.
    (integer :closed)
    (decimal :has_amount)
    (string :has_currency) ; Currency code.
    (string :has_payment_type) ; Payment type code.
    (decimal :wants_amount)
    (string :wants_currency) ; Currency code.
    (string :wants_payment_type))
  
  (apply insert-into :offers (execute-query ["SELECT * FROM offers_decimal"]))
  (drop-table :offers_decimal))
  
(defn
#^{:doc "Migrates the database down from version 8."}
  down []
  (drop-table :offers_int)
  (create-table :offers_int
    (id)
    (date-time :created_at)
    (belongs-to :user)
    (integer :foreign_offer_id) ; The id of the matching trade on the other system.
    (integer :closed)
    (integer :has_amount)
    (string :has_currency) ; Currency code.
    (string :has_payment_type) ; Payment type code.
    (integer :wants_amount)
    (string :wants_currency) ; Currency code.
    (string :wants_payment_type))

  (when (table-exists? :offers)
    (apply insert-into :offers_int (execute-query ["SELECT * FROM offers"]))
    (drop-table :offers))

  (create-table :offers
    (id)
    (date-time :created_at)
    (belongs-to :user)
    (integer :foreign_offer_id) ; The id of the matching trade on the other system.
    (integer :closed)
    (integer :has_amount)
    (string :has_currency) ; Currency code.
    (string :has_payment_type) ; Payment type code.
    (integer :wants_amount)
    (string :wants_currency) ; Currency code.
    (string :wants_payment_type))

  (apply insert-into :offers (execute-query ["SELECT * FROM offers_int"]))
  (drop-table :offers_int))