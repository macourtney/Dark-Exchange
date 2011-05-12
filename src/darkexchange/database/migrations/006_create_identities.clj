(ns darkexchange.database.migrations.006-create-identities
  (:use darkexchange.database.util))

(defn
#^{:doc "Creates the identities table in the database."}
  up []
  (create-table :identities
    (id)
    (string :name)
    (string :public-key)
    (belongs-to :peer)))
  
(defn
#^{:doc "Drops the identities table in the database."}
  down []
  (drop-table :identities))