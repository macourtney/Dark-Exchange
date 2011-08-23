(ns darkexchange.model.identity
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.contrib.logging :as logging]
            [darkexchange.model.client :as client]
            [darkexchange.model.peer :as peer]
            [darkexchange.model.security :as security]
            [darkexchange.model.terms :as terms]
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
  (:associations (belongs-to peer)
                 (has-many scorer_trust_scores :fk target_id)
                 (has-many target-trust-scores :fk target_id :model trust-score))
  (:callbacks (:after-update identity-update)
              (:after-insert identity-add)
              (:after-destroy identity-delete)))

(defn add-identity [user-name public-key public-key-algorithm destination]
  (when-let [peer (peer/find-peer destination)]
    (insert { :name user-name :public_key public-key :public_key_algorithm public-key-algorithm :peer_id (:id peer)
              :is_online 1 })))

(defn update-identity-name [current-identity user-name]
  (when (not (= user-name (:name current-identity)))
    (update { :id (:id current-identity) :name user-name })))

(defn update-identity-peer [current-identity destination]
  (when-let [peer (peer/find-peer destination)]
    (when (not (= (:id peer) (:peer_id current-identity)))
      (update { :id (:id current-identity) :peer_id (:id peer) }))))

(defn update-identity-is-online [current-identity is-online]
  (when current-identity
    (update { :id (:id current-identity) :is_online (if is-online 1 0) })))

(defn update-identity [current-identity user-name destination]
  (update-identity-name current-identity user-name)
  (update-identity-peer current-identity destination)
  (update-identity-is-online current-identity true)
  (:id current-identity))

(defn find-identity [user-name public-key public-key-algorithm]
  (find-record { :name user-name :public_key public-key :public_key_algorithm public-key-algorithm }))

(defn find-identity-by-peer [peer]
  (when peer
    (find-record { :peer_id (:id peer) })))

(defn find-identity-by-destination [destination]
  (find-identity-by-peer (peer/find-peer destination)))

(defn add-or-update-identity [user-name public-key public-key-algorithm destination]
  (if-let [current-identity (find-identity user-name public-key public-key-algorithm)]
    (update-identity current-identity user-name destination)
    (add-identity user-name public-key public-key-algorithm destination)))

(defn identity-not-online [destination]
  (update-identity-is-online (find-identity-by-destination destination) false))

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
      (when-let [peer-id (:peer_id target-identity)]
        (when-let [peer (peer/find-record { :id peer-id })]
          (peer/destination-for peer))))))

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

(defn shortened-public-key-str [public-key]
  (when public-key
    (if (> (.length public-key) 60)
      (str ".." (.substring public-key 40 60) "..")
      "..")))

(defn shortened-public-key [identity]
  (shortened-public-key-str (:public_key identity)))

(defn identity-text [identity]
  (str (:name identity) " (" (shortened-public-key identity) ")"))

(defn is-online? [identity]
  (as-boolean (:is_online identity)))

(defn table-identity [identity]
  (let [destination (destination-for identity)]
    (merge (select-keys identity [:id :name :public_key_algorithm]) 
      { :destination destination
        :public_key (shortened-public-key identity)
        :is_online (when (is-online? identity) (terms/yes)) })))

(defn all-online-identities []
  (find-records ["is_online = 1"]))

(defn all-identities
  ([] (all-identities false))
  ([only-online-identities]
    (if only-online-identities
      (all-online-identities)
      (find-records [true]))))

(defn non-user-identities
  ([] (non-user-identities false))
  ([only-online-identities]
    (if-let [user-identity (current-user-identity)]
      (filter #(not (= (:id user-identity) (:id %))) (all-identities only-online-identities))
      (all-identities only-online-identities))))

(defn is-user-identity? [identity]
  (if (and (:name identity) (:public_key identity) (:public_key_algorithm identity))
    (let [user (user/current-user)]
      (and
        (= (:name identity) (:name user))
        (= (:public_key identity) (:public_key user))
        (= (:public_key_algorithm identity) (:public_key_algorithm user))))
    (= (:id identity) (:id (current-user-identity)))))

(defn table-identities [only-online-identities]
  (map table-identity (non-user-identities only-online-identities)))

(defn get-table-identity [id]
  (table-identity (get-record id)))