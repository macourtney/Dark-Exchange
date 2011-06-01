(ns test.fixtures.util
  (:use darkexchange.database.util)
  (:require [clojure.test :as clojure-test] 
            [test.init :as test-init]))

(defn load-records [fixture-map]
  (apply insert-into (:table fixture-map) (:records fixture-map)))

(defn unload-records [fixture-map]
  (try
    (delete (:table fixture-map) ["true"])
    (catch Throwable t
      (println "An error occured while trying to empty table:" (:table fixture-map))))) 

(defn run-fixture [fixture-map function]
  (try
    (load-records fixture-map) 
    (function)
    (finally
      (unload-records fixture-map))))

(defn build-table-map [table-map fixture-maps]
  (if-let [fixture-map (first fixture-maps)]
    (let [new-table-map (assoc table-map (:table fixture-map) fixture-map)]
      (recur new-table-map (concat (rest fixture-maps)
                                             (filter #(not (contains? new-table-map (:table %1)))
                                               (:required-fixtures fixture-map)))))
    table-map)) 

(defn create-fixture [fixture-maps]
  (clojure-test/join-fixtures (map #(partial run-fixture %1) (vals (build-table-map {} fixture-maps)))))

(defn use-fixture-maps [fixture-type & fixture-maps]
  (clojure-test/use-fixtures fixture-type (create-fixture fixture-maps)))