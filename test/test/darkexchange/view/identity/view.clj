(ns test.darkexchange.view.identity.view
  (:use clojure.contrib.test-is
        darkexchange.view.identity.view)
  (:import [javax.swing JFrame]))

(deftest test-create
  (create (new JFrame)))