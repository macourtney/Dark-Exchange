(ns test.darkexchange.model.peer
  (:require [test.darkexchange.util :as test-util]
            [test.fixtures.peer :as peer-fixture] 
            [test.fixtures.util :as fixtures-util]) 
  (:use clojure.contrib.test-is
        darkexchange.model.peer))

(fixtures-util/use-fixture-maps :once peer-fixture/fixture-map)

(deftest test-all-peers
  (let [all-peers-seq (all-peers)]
    (is all-peers-seq)
    (is (first all-peers-seq))
    (is (string? (:destination (first all-peers-seq))))
    (is (= (:destination (first peer-fixture/records)) (:destination (first all-peers-seq))))))