(ns darkexchange.model.user
  (:require [clj-record.boot :as clj-record-boot]
            [darkexchange.model.security :as security])
  (:use darkexchange.model.base)
  (:import [java.sql Clob]))

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
    (reduce #(clob-clean-up user %) (conj keys key))))

(defn user-clean-up [user]
  (clob-clean-up user :public_key :private_key))

(defn encrypt-password [user]
  (let [salt (security/create-salt)]
    (merge user { :encrypted-password (security/encrypt-password-string (:password user) salt) :salt (str salt)})))

(defn generate-fields [user]
  (select-keys (encrypt-password user) [:name :encrypted-password :salt :public-key :private-key]))

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
    (some #(not (= (first %1) (second %1))) (map #(list %1 %2) array1 array2))))

(defn validate-passwords [password1 password2]
  (when (char-arrays-equals? password1 password2)
    password1))