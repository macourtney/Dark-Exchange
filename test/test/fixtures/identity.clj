(ns test.fixtures.identity
  (:use darkexchange.database.util)
  (:require [test.fixtures.peer :as peer-fixture] 
            [test.fixtures.util :as fixtures-util]))

(def records [
  { :id 1
    :name "test-identity"
    :public_key "blah"
    :public_key_algorithm "RSA"
    :peer_id 1 }
  { :id 2
    :name "test-user"
    :public_key ""
    :public_key_algorithm "RSA"
    :peer_id 1 }
  { :id 3
    :name "test-identity2"
    :public_key "blah2"
    :public_key_algorithm "RSA"
    :peer_id 1 }
  { :id 4
    :name "test-identity3"
    :public_key "blah3"
    :public_key_algorithm "RSA"
    :peer_id 1 }])

(def fixture-table-name :identities)

(def fixture-map { :table fixture-table-name :records records :required-fixtures [peer-fixture/fixture-map] })