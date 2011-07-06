(ns darkexchange.database.migrations.008-switch-to-big-decimal
  (:use darkexchange.database.util))

(defn
#^{:doc "Migrates the database up to version 8."}
  up []
  (execute-update ["ALTER TABLE offers ALTER COLUMN has_amount DECIMAL(20, 10)"])
  (execute-update ["ALTER TABLE offers ALTER COLUMN wants_amount DECIMAL(20, 10)"]))
  
(defn
#^{:doc "Migrates the database down from version 8."}
  down []
  (execute-update ["ALTER TABLE offers ALTER COLUMN has_amount INT"])
  (execute-update ["ALTER TABLE offers ALTER COLUMN wants_amount INT"]))