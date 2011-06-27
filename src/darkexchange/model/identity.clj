(ns darkexchange.model.identity
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.contrib.logging :as logging]
            [darkexchange.model.client :as client]
            [darkexchange.model.peer :as peer]
            [darkexchange.model.security :as security]
            [darkexchange.model.user :as user])
  (:use darkexchange.model.base)
  (:import [org.apache.commons.codec.binary Base64]))

(def identity-add-listeners (atom []))

(def identity-update-listeners (atom []))

(def identity-delete-listeners (atom []))

(defn add-identity-add-listener [listener]
  (swap! identity-add-listeners conj listener))

(defn add-identity-update-listener [listener]
  (swap! identity-update-listeners conj listener))

(defn add-identity-delete-listener [listener]
  (swap! identity-delete-listeners conj listener))

(defn identity-add [identity]
  (doseq [listener @identity-add-listeners]
    (listener identity)))

(defn identity-update [identity]
  (doseq [listener @identity-update-listeners]
    (listener identity)))

(defn identity-delete [identity]
  (doseq [listener @identity-delete-listeners]
    (listener identity)))

(clj-record.core/init-model
  (:associations (belongs-to peer))
  (:callbacks (:after-update identity-update)
              (:after-insert identity-add)
              (:after-destroy identity-delete)))

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

(defn decode-base64 [string]
  (when string
    (.decode (new Base64) string)))

(defn public-key [target-identity]
  (security/decode-public-key { :algorithm (:public_key_algorithm target-identity)
                                :bytes (decode-base64 (:public_key target-identity)) }))

(defn verify-signature [target-identity data signature]
  (security/verify-signature (public-key target-identity) data (decode-base64 signature)))

(defn current-user-identity []
  (let [user (user/current-user)]
    (find-identity (:name user) (:public_key user) (:public_key_algorithm user))))

(defn table-identity [identity]
  (assoc (select-keys identity [:id :name :public_key :public_key_algorithm]) :destination (destination-for identity)))

(defn table-identities []
  (map table-identity (find-records [true])))