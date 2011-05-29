(ns test.fixtures.offer
  (:use darkexchange.database.util)
  (:require [test.init :as test-init]
            [test.fixtures.user :as user-fixture])
  (:import [java.util Date]))

(def records [
  { :id 1
    :created_at (new Date)
    :acceptor_id nil
    :user_id 1
    :accept_confirm 0 }])

(def fixture-table-name :offers) 

(defn offer-fixture [function]
  (try
    (apply insert-into fixture-table-name records)
    (function)
    (finally
      (delete fixture-table-name ["true"])))) 

(defn fixture [function]
  (user-fixture/fixture #(offer-fixture function)))