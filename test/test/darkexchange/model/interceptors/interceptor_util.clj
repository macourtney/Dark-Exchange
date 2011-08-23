(ns test.darkexchange.model.interceptors.interceptor-util 
  (:use clojure.contrib.test-is
        darkexchange.model.interceptors.interceptor-util))

(deftest test-run-interceptors
  (is (run-interceptors [] not false))
  (is (not (run-interceptors [#(not (%1 %2))] not false))))