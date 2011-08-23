(ns test.darkexchange.controller.identity.view
  (:require [test.fixtures.identity :as identity-fixture]
            [test.fixtures.offer :as offer-fixture]
            [test.fixtures.trust-score :as trust-score-fixture]
            [test.fixtures.util :as fixtures-util])
  (:use clojure.contrib.test-is
        darkexchange.controller.identity.view)
  (:import [javax.swing JFrame]))

(fixtures-util/use-fixture-maps :once offer-fixture/fixture-map trust-score-fixture/fixture-map)

(deftest test-create-and-initialize
  (create-and-initialize (new JFrame) (first identity-fixture/records)))