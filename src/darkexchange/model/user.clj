(ns darkexchange.model.user
  (:require [clj-record.boot :as clj-record-boot])
  (:use darkexchange.model.base))

(defn clob-clean-up 
  ([user key] 
    (let [clob (get user key)]
      (if (instance? Clob clob)
        (assoc user key (load-clob clob))
        user)))
  ([user & keys]
    (reduce #(clob-clean-up user %) keys)))

(defn user-clean-up [user]
  (clob-clean-up user :public_key :private_key))

(clj-record.core/init-model
  (:callbacks (:after-load user-clean-up)))

