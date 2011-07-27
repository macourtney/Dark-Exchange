(ns darkexchange.model.trade-message
  (:require [clojure.contrib.logging :as logging]
            [clj-record.boot :as clj-record-boot]
            [darkexchange.model.identity :as identity])
  (:use darkexchange.model.base)
  (:import [java.util Date]))

(def message-add-listeners (atom []))

(defn add-message-add-listener [listener]
  (swap! message-add-listeners conj listener))

(defn remove-message-add-listener [listener]
  (swap! message-add-listeners remove-listener listener))

(defn message-added [trade-message]
  (doseq [listener @message-add-listeners]
    (listener trade-message)))

(defn trade-message-clean-up [trade-message]
  (clean-clob-key trade-message :body))

(clj-record.core/init-model
  (:associations (belongs-to trade)
                 (belongs-to identity))
  (:callbacks (:after-insert message-added)
              (:after-load trade-message-clean-up)))

(defn create-new-message
  ([body trade] (create-new-message body trade nil (identity/current-user-identity)))
  ([body trade foreign-message-id from-identity]
    (when (and body trade from-identity)
      (insert { :created_at (new Date) :body body :trade_id (:id trade)  :foreign_message_id foreign-message-id
                :identity_id (:id from-identity) }))))

(defn create-message-to-send [body trade]
  (when-let [trade-message-id (create-new-message body trade)]
    (update { :id trade-message-id :seen 1 :viewed 1})))

(defn update-foreign-message-id [message-id foreign-message-id]
  (update { :id message-id :foreign_message_id foreign-message-id }))

(defn as-table-trade-message [trade-message]
  { :id (:id trade-message) :from (:name (find-identity trade-message)) :date-received (:created_at trade-message)
   :seen (:seen trade-message) })

(defn update-or-create-message [trade-id message from-identity]
  (if-let [foreign-message-id (:foreign_message_id message)]
    (update-foreign-message-id foreign-message-id (:id message))
    (when-not (find-record { :foreign_message_id (:id message) })
      (create-new-message (:body message) { :id trade-id } (:id message) from-identity))))

(defn find-matching-messages [messages]
  (map #(find-record { :foreign_message_id (:id %1)}) messages))

(defn seen? [trade-message]
  (as-boolean (:seen trade-message)))

(defn has-unseen-message? [trade-id]
  (find-record ["trade_id = ? AND (seen IS NULL OR seen = 0) LIMIT 1" trade-id]))

(defn mark-as-seen [trade-messages]
  (doseq [trade-message trade-messages]
    (when-not (seen? trade-message) (update { :id (:id trade-message) :seen 1 }))))