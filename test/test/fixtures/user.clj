(ns test.fixtures.user
  (:use darkexchange.database.util)
  (:require [test.init :as test-init]))

(def records [
  { :id 1
    :name "test-user"
    :encrypted_password "nTsg+WsHHNURODpaVcbK1Ip8UHY=" ; the test password is "password"
    :salt "197849973"
    :public_key ""
    :private_key "" }])

(def fixture-table-name :users)

(defn fixture [function]
  (try
    (apply insert-into fixture-table-name records)
    (function)
    (finally
      (delete fixture-table-name ["true"]))))