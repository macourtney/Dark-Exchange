(ns darkexchange.database.migrations.004-create-identities
  (:use darkexchange.database.util))

(defn
#^{:doc "Creates the identities table in the database."}
  up []
  (create-table :identities
    (id)
    (string :name)
    (string :public_key)
    (string :public_key_algorithm)
    (belongs-to :peer)))
  
(defn
#^{:doc "Drops the identities table in the database."}
  down []
  (drop-table :identities))