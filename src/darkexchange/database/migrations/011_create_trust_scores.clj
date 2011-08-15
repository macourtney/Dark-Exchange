(ns darkexchange.database.migrations.011-create-trust-scores
  (:use darkexchange.database.util))

(defn
#^{ :doc "Creates the trust scores table in the database." }
  up []
  (create-table :trust_scores
    (id)
    (belongs-to :scorer)
    (belongs-to :target)
    (decimal :basic { :precision 5 :scale 1 })
    (decimal :combined { :precision 15 :scale 10 })))
  
(defn
#^{ :doc "Drops the trust scores table in the database." }
  down []
  (drop-table :trust_scores))