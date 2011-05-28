(ns test.darkexchange.model.user
  (:use clojure.contrib.test-is
        test.fixtures.user
        darkexchange.model.user))

(def model "user")

(use-fixtures :once fixture)

(deftest test-first-record
  (is (get-record 1)))