(ns test.darkexchange.model.security
  (:require [clojure.contrib.logging :as logging]) 
  (:use [darkexchange.model.security])
  (:use [clojure.test])
  (:import [java.security KeyPair]))

(deftest test-encrypt-decrypt
  (let [key-pair (generate-key-pair)
        data "secret text"]
    (is key-pair "key-pair is nil, but expected non-nil.")
    (let [encrypted-text (encrypt key-pair data)]
      (is encrypted-text "encrypted-text is nil, but expected non-nil.")
      (let [decrypted-text (decrypt key-pair encrypted-text)]
        (is decrypted-text "decrypted-text is nil, but expected non-nil.")
        (is (= decrypted-text data) (str "Text not decrypted."))))))

(deftest password-encrypt-decrypt
  (let [password "password"
        data "secret text"
        algorithm des-algorithm
        encrypted-data (password-encrypt password data algorithm)]
    (is (not (= data encrypted-data)) "Text not encrypted.")
    (is (= data (password-decrypt password encrypted-data algorithm)) "Text not decrypted."))
  (let [password "password blah blah blah blah blah blah blah blah"
        data "secret text"
        algorithm des-algorithm
        encrypted-data (password-encrypt password data algorithm)]
    (is (not (= data encrypted-data)) "Text not encrypted.")
    (is (= data (password-decrypt password encrypted-data algorithm)) "Text not decrypted.")))

(deftest basic-password-protection
  (let [password "password"
        salt 2079324
        algorithm default-encrypt-password-algorithm
        n default-encrypt-password-n
        encrypted-password (encrypt-password-string password salt algorithm n)]
    (is (not (= encrypted-password password)) "Password not encrypted") 
    (is (= encrypted-password (encrypt-password-string password salt algorithm n)) "Password check not valid.")
    (is (not (= encrypted-password (encrypt-password-string password salt algorithm 1))) "Multiple hash iterations do not help.")
    (is (not (= encrypted-password (encrypt-password-string (.substring password 0 4) salt algorithm n))) "Short password works when it shouldn't.")))

(defn byte-not-equals [byte1 byte2]
  (not (= byte1 byte2))) 

(defn byte-array-equals [byte-array1 byte-array2]
  (and
    (= (count byte-array1) (count byte-array2))
    (nil? (some identity (map byte-not-equals byte-array1 byte-array2))))) 

(defn key-equals? [key1 key2]
  (and
    (= (.getAlgorithm key1) (.getAlgorithm key2))
    (= (.getFormat key1) (.getFormat key2))
    (byte-array-equals (.getEncoded key1) (.getEncoded key2)))) 

(defn key-pair-equals? [key-pair1 key-pair2]
  (and
    (key-equals? (.getPublic key-pair1) (.getPublic key-pair2))
    (key-equals? (.getPrivate key-pair1) (.getPrivate key-pair2)))) 

(deftest save-load-key-pairs
  (let [key-pair (generate-key-pair)
        key-pair-map (get-key-pair-map key-pair)]
    (is key-pair-map "Expected non-nil key-pair-map.")
    (is (map? key-pair-map) "key-pair-map must be a map")
    (is (contains? key-pair-map :public-key) "key-pair-map must contain the :public-key key")
    (is (contains? key-pair-map :private-key) "key-pair-map must contain the :private-key key")
    (let [public-key (:public-key key-pair-map)
          private-key (:private-key key-pair-map)]
      (is (map? public-key) "Public key must be a map.")
      (is (map? private-key) "private key must be a map.")
      (is (= (:algorithm public-key) default-algorithm) "The public key algorithm must be the default algorithm.")
      (is (= (:algorithm private-key) default-algorithm) "The private key algorithm must be the default algorithm.")
      (is (contains? public-key :bytes) "The public key map must contain the :bytes key.")
      (is (contains? private-key :bytes) "The private key map must contain the :bytes key."))
    (let [decoded-key-pair (decode-key-pair key-pair-map)]
      (is decoded-key-pair "Expected non-nil decoded-key-pair")
      (is (instance? KeyPair decoded-key-pair) "decode-key-pair must return an object of type KeyPair.")
      (is (key-pair-equals? key-pair decoded-key-pair) "The decoded key is not equal to the original key."))))

(deftest signature
  (let [test-data "Test data to sign"
        key-pair (generate-key-pair)
        signature (sign key-pair test-data)]
    (is (verify-signature key-pair test-data signature))
    (is (not (verify-signature (generate-key-pair) test-data signature)))))