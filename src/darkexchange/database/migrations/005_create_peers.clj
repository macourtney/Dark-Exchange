(ns darkexchange.database.migrations.005-create-peers
  (:use darkexchange.database.util))

(defn
#^{:doc "Creates the peers table in the database."}
  up []
  (create-table :peers
    (id)
    (text :destination)
    (date-time :created-at)
    (date-time :updated-at)))
  
(defn
#^{:doc "Drops the peers table in the database."}
  down []
  (drop-table :peers))