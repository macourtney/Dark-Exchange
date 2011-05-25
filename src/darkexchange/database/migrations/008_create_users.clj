(ns darkexchange.database.migrations.008-create-users
  (:use darkexchange.database.util))

(defn
#^{:doc "Creates the users table in the database."}
  up []
  (create-table :users
    (id)
    (string :name)
    (string :encrypted-password)
    (string :salt)
    (text :public-key)
    (text :private-key)))
  
(defn
#^{:doc "Drops the users table in the database."}
  down []
  (drop-table :users))