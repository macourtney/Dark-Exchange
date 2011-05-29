(ns test.darkexchange.model.identity
  (:require [test.fixtures.identity :as identity-fixture]
            [test.fixtures.peer :as peer-fixture]) 
  (:use clojure.contrib.test-is
        darkexchange.model.identity))

(use-fixtures :once identity-fixture/fixture)

(deftest test-add-identity
  (let [identity-name "add-identity"
        public-key "test public key"
        peer (first peer-fixture/records)
        id (add-identity identity-name public-key (:destination peer))]
    (is id "Identity not added.")
    (let [new-identity (get-record id)]
      (is (= identity-name (:name new-identity)) "Incorrect name for new identity.")
      (is (= public-key (:public_key new-identity)) "Incorrect public key for new identity.")
      (is (= (:id peer) (:peer_id new-identity)) "Incorrect peer id for new identity."))
    (destroy-record { :id id }))
  (is (not (add-identity "blah" "blah" "blah")) "Indentity created with invalid peer."))

(deftest test-update-identity-name
  (let [identity-name "update-identity-name"
        peer (first peer-fixture/records)
        id (add-identity identity-name "" (:destination peer))]
    (is id "Test identity was not added.")
    (let [current-identity (get-record id)
          new-name "new-name"]
      (update-identity-name current-identity new-name)
      (let [new-identity (get-record id)] 
        (is (= new-name (:name new-identity)) "Name not updated for the test identity.")))
    (destroy-record { :id id })))

(deftest test-update-identity-peer
  (let [peer (first peer-fixture/records)
        id (add-identity "blah" "" (:destination peer))]
    (is id "Test identity was not added.")
    (let [current-identity (get-record id)
          new-peer (second peer-fixture/records)]
      (update-identity-peer current-identity (:destination new-peer))
      (let [new-identity (get-record id)] 
        (is (= (:id new-peer) (:peer_id new-identity)) "Peer not updated for the test identity.")))
    (destroy-record { :id id })))

(deftest test-update-identity
  (let [identity-name "update-identity-name"
        peer (first peer-fixture/records)
        id (add-identity identity-name "" (:destination peer))]
    (is id "Test identity was not added.")
    (let [current-identity (get-record id)
          new-peer (second peer-fixture/records)
          new-name "new-name"]
      (is (= id (update-identity current-identity new-name (:destination new-peer))) "Expected id returned from update identity.")
      (let [new-identity (get-record id)]
        (is (= new-name (:name new-identity)) "Name not updated for the test identity.")
        (is (= (:id new-peer) (:peer_id new-identity)) "Peer not updated for the test identity.")))
    (destroy-record { :id id })))

(deftest test-add-or-update-identity
  (let [identity-name "add-identity"
        public-key "test public key"
        peer (first peer-fixture/records)]
    (is (nil? (find-record { :name identity-name  :public_key public-key :peer_id (:id peer) }))
      "Duplicate identity in database. Could not add the test identity.") 
    (let [id (add-or-update-identity identity-name public-key (:destination peer))]
      (is id "Identity not added.")
      (let [new-identity (get-record id)]
        (is (= identity-name (:name new-identity)) "Incorrect name for new identity.")
        (is (= public-key (:public_key new-identity)) "Incorrect public key for new identity.")
        (is (= (:id peer) (:peer_id new-identity)) "Incorrect peer id for new identity.")
        (let [new-peer (second peer-fixture/records)
              new-name "new-name"]
          (is (= id (update-identity new-identity new-name (:destination new-peer))) "Expected id returned from update identity.")
          (let [final-identity (get-record id)]
            (is (= new-name (:name final-identity)) "Name not updated for the test identity.")
            (is (= (:id new-peer) (:peer_id final-identity)) "Peer not updated for the test identity."))))
      (destroy-record { :id id }))))