(ns darkexchange.model.identity
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.contrib.logging :as logging]
            [darkexchange.model.peer :as peer-model])
  (:use darkexchange.model.base))

(clj-record.core/init-model
  (:associations (belongs-to peer)))

(defn add-identity [identity-name public-key destination]
  (when-let [peer (peer-model/find-peer destination)]
    (insert { :name identity-name :public_key public-key :peer_id (:id peer) })))

(defn update-identity-name [current-identity identity-name]
  (when (not (= identity-name (:name current-identity)))
    (update { :id (:id current-identity) :name identity-name })))

(defn update-identity-peer [current-identity destination]
  (when-let [peer (peer-model/find-peer destination)]
    (when (not (= (:id peer) (:peer_id current-identity)))
      (update { :id (:id current-identity) :peer_id (:id peer) }))))

(defn update-identity [current-identity identity-name destination]
  (update-identity-name current-identity identity-name)
  (update-identity-peer current-identity destination)
  (:id current-identity))

(defn add-or-update-identity [identity-name public-key destination]
  (if-let [current-identity (find-record { :public_key public-key })]
    (update-identity current-identity identity-name destination)
    (add-identity identity-name public-key destination)))