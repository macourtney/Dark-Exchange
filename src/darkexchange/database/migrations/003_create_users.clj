(ns darkexchange.database.migrations.003-create-users
  (:use darkexchange.database.util))

(defn
#^{:doc "Creates the users table in the database."}
  up []
  (create-table :users
    (id)
    (string :name)
    (string :encrypted_password)
    (string :salt)
    (string :encrypted_password_algorithm)
    (integer :encrypted_password_n)
    (text :public_key)
    (string :public_key_algorithm)
    (text :private_key)
    (string :private_key_algorithm)
    (string :private_key_encryption_algorithm)))
  
(defn
#^{:doc "Drops the users table in the database."}
  down []
  (drop-table :users))