(ns test.fixtures.identity
  (:use darkexchange.database.util)
  (:require [test.fixtures.peer :as peer-fixture] 
            [test.fixtures.util :as fixtures-util]))

(def records [
  { :id 1
    :name "test-identity"
    :public_key "blah" 
    :peer_id 1 }])

(def fixture-table-name :identities)

(def fixture-map { :table fixture-table-name :records records :required-fixtures [peer-fixture/fixture-map] })