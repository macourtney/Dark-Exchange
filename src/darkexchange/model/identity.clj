(ns darkexchange.model.identity
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.contrib.logging :as logging]
            [darkexchange.model.client :as client]
            [darkexchange.model.peer :as peer])
  (:use darkexchange.model.base))

(clj-record.core/init-model
  (:associations (belongs-to peer)))

(defn add-identity [user-name public-key public-key-algorithm destination]
  (when-let [peer (peer/find-peer destination)]
    (insert { :name user-name :public_key public-key :public_key_algorithm public-key-algorithm :peer_id (:id peer) })))

(defn update-identity-name [current-identity user-name]
  (when (not (= user-name (:name current-identity)))
    (update { :id (:id current-identity) :name user-name })))

(defn update-identity-peer [current-identity destination]
  (when-let [peer (peer/find-peer destination)]
    (when (not (= (:id peer) (:peer_id current-identity)))
      (update { :id (:id current-identity) :peer_id (:id peer) }))))

(defn update-identity [current-identity user-name destination]
  (update-identity-name current-identity user-name)
  (update-identity-peer current-identity destination)
  (:id current-identity))

(defn add-or-update-identity [user-name public-key public-key-algorithm destination]
  (if-let [current-identity (find-record { :public_key public-key })]
    (update-identity current-identity user-name destination)
    (add-identity user-name public-key public-key-algorithm destination)))

(defn find-identity [user-name public-key public-key-algorithm]
  (find-record { :name user-name :public_key public-key :public_key_algorithm public-key-algorithm }))

(defn find-or-create-identity [user-name public-key public-key-algorithm destination]
  (when (and user-name public-key destination)
    (or 
      (find-identity user-name public-key public-key-algorithm)
      (get-record (add-identity user-name public-key public-key-algorithm destination)))))

(defn destination-for
  ([user-name public-key public-key-algorithm]
    (destination-for (find-identity user-name public-key public-key-algorithm)))
  ([target-identity]
    (when target-identity
      (peer/destination-for (find-peer target-identity)))))

(defn send-message 
  ([user-name public-key public-key-algorithm action data]
    (send-message (find-identity user-name public-key public-key-algorithm) action data))
  ([target-identity action data]
    (client/send-message (destination-for target-identity) action data)))