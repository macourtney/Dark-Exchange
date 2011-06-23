(ns test.fixtures.user
  (:use darkexchange.database.util)
  (:require [test.init :as test-init]
            [test.fixtures.util :as fixtures-util]))

(def records [
  { :id 1
    :name "test-user"
    :encrypted_password "47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=" ; the test password is "password" 
    :salt "2145354708"
    :encrypted_password_algorithm "SHA-256"
    :encrypted_password_n 1000
    :public_key ""
    :public_key_algorithm "RSA" 
    :private_key ""
    :private_key_algorithm "RSA"
    :private_key_encryption_algorithm "DES" }])

(def fixture-table-name :users)

(def fixture-map { :table fixture-table-name :records records })