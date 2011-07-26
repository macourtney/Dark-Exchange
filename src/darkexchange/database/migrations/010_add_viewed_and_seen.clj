(ns darkexchange.database.migrations.010-add-viewed-and-seen
  (:use darkexchange.database.util))

(defn
#^{ :doc "Adds the viewed and seen columns to the trades table." }
  up []
  (add-column :trade-messages (integer :seen))
  (add-column :trade-messages (integer :viewed)))
  
(defn
#^{ :doc "Removes the viewed and seen columns from the trades table." }
  down []
  (drop-column :trade-messages :seen)
  (drop-column :trade-messages :viewed))