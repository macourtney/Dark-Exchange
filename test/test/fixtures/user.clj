(ns test.fixtures.user
  (:use darkexchange.database.util)
  (:require [test.init :as test-init]
            [test.fixtures.util :as fixtures-util]))

(def records [
  { :id 1
    :name "test-user"
    :encrypted_password "nTsg+WsHHNURODpaVcbK1Ip8UHY=" ; the test password is "password"
    :salt "197849973"
    :public_key ""
    :private_key "" }])

(def fixture-table-name :users)

(def fixture-map { :table fixture-table-name :records records })