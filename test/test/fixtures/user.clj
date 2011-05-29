(ns test.fixtures.user
  (:use darkexchange.database.util)
  (:require [test.init :as test-init]))

(def records [
  { :id 1
    :name "test-user"
    :encrypted_password ""
    :salt ""
    :public_key ""
    :private_key "" }])

(defn fixture [function]
  (apply insert-into :users records)
  (function)
  (delete :users ["true"]))