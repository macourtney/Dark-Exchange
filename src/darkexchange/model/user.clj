(ns darkexchange.model.user
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.contrib.logging :as logging]
            [darkexchange.model.security :as security])
  (:use darkexchange.model.base)
  (:import [java.sql Clob]
           [org.apache.commons.codec.binary Base64]))

(def saved-current-user (atom nil))

(def user-add-listeners (atom []))

(defn add-user-add-listener [user-add-listener]
  (swap! user-add-listeners conj user-add-listener))

(defn clob-clean-up 
  ([user key] 
    (let [clob (get user key)]
      (if (instance? Clob clob)
        (assoc user key (load-clob clob))
        user)))
  ([user key & keys]
    (reduce #(clob-clean-up %1 %2) user (conj keys key))))

(defn user-clean-up [user]
  (clob-clean-up user :public_key :private_key))

(defn password-str [password]
  (if (string? password)
    password
    (String. password)))

(defn encrypt-password [user]
  (let [salt (security/create-salt)]
    (merge user
      { :encrypted_password (security/encrypt-password-string (password-str (:password user)) salt)
        :salt (str salt) })))

(defn char-array-to-bytes [char-array-data]
  (byte-array (flatten (map #(seq (.getBytes (Character/toString %1) "UTF-8")) char-array-data))))

(defn char-array-to-string [char-array-data]
  (String. char-array-data))

(defn encrypt-private-key [password key-bytes]
  (Base64/encodeBase64String
    (security/password-encrypt (char-array-to-string password) (Base64/encodeBase64String key-bytes))))

(defn generate-keys [user]
  (let [key-pair (security/generate-key-pair)
        key-pair-map (security/get-key-pair-map key-pair)]
    (merge user { :public_key (Base64/encodeBase64String (:bytes (:public-key key-pair-map)))
                  :public_key_algorithm (:algorithm (:public-key key-pair-map))
                  :private_key (encrypt-private-key (:password user) (:bytes (:private-key key-pair-map)))
                  :private_key_algorithm (:algorithm (:private-key key-pair-map)) })))

(defn generate-fields [user]
  (select-keys (generate-keys (encrypt-password user))
    [:name :encrypted_password :salt :public_key :public_key_algorithm :private_key :private_key_algorithm]))

(defn call-user-add-listeners [user]
  (doseq [user-add-listener @user-add-listeners]
    (user-add-listener user)))

(clj-record.core/init-model
  (:callbacks (:after-insert call-user-add-listeners)
              (:after-load user-clean-up)
              (:before-insert generate-fields)))

(defn all-users []
  (find-records [true]))

(defn all-user-names []
  (map :name (all-users)))

(defn find-user-by-name [user-name]
  (find-record { :name user-name }))

(defn validate-user-name [user-name]
  (when (and user-name (> (count user-name) 0) (not (find-user-by-name user-name)))
    user-name))

(defn char-arrays-equals? [array1 array2]
  (and (= (count array1) (count array2))
    (nil? (some #(not %1) (map #(= %1 %2) array1 array2)))))

(defn validate-passwords [password1 password2]
  (when (char-arrays-equals? password1 password2)
    password1))

(defn create-user [user-name password]
  (insert { :name user-name :password password }))

(defn login [user-name password]
  (when-let [user (find-user-by-name user-name)]
    (when (= (:encrypted_password user) (security/encrypt-password-string (password-str password) (:salt user)))
      (reset! saved-current-user (assoc user :password password))
      @saved-current-user)))

(defn logout []
  (reset! saved-current-user nil))

(defn current-user []
  @saved-current-user)

(defn decode-base64 [string]
  (when string
    (.decode (new Base64) string)))

(defn public-key-bytes [user]
  (decode-base64 (:public_key user)))

(defn public-key-map [user]
  { :algorithm (:public_key_algorithm user)  :bytes (public-key-bytes user) })

(defn private-key-bytes [user]
  (decode-base64
    (security/password-decrypt (char-array-to-string (:password user)) (decode-base64 (:private_key user)))))

(defn private-key-map [user]
  { :algorithm (:private_key_algorithm user) :bytes (private-key-bytes user) })

(defn current-user-key-pair []
  (let [user (current-user)
        public-key-map (public-key-map user)
        private-key-map (private-key-map user)]
    (security/decode-key-pair { :public-key public-key-map :private-key private-key-map })))

(defn sign [data]
  (Base64/encodeBase64String (security/sign (current-user-key-pair) data)))

(defn verify [data signature]
  (security/verify-signature (current-user-key-pair) data (decode-base64 signature)))