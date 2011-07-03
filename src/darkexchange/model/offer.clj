(ns darkexchange.model.offer
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.contrib.logging :as logging]
            [darkexchange.model.currency :as currency]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.payment-type :as payment-type]
            [darkexchange.model.user :as user])
  (:use darkexchange.model.base)
  (:import [java.util Date]))

(def offer-add-listeners (atom []))
(def delete-offer-listeners (atom []))
(def update-offer-listeners (atom []))

(defn add-offer-add-listener [listener]
  (swap! offer-add-listeners conj listener))

(defn add-delete-offer-listener [listener]
  (swap! delete-offer-listeners conj listener))

(defn add-update-offer-listener [listener]
  (swap! update-offer-listeners conj listener))

(defn offer-add [offer]
  (doseq [listener @offer-add-listeners]
    (listener offer)))

(defn offer-deleted [offer]
  (doseq [listener @delete-offer-listeners]
    (listener offer)))

(defn offer-updated [offer]
  (doseq [listener @update-offer-listeners]
    (listener offer)))

(clj-record.core/init-model
  (:associations (belongs-to user))
  (:callbacks (:after-insert offer-add)
              (:after-destroy offer-deleted)
              (:after-update offer-updated)))

(defn create-new-offer [offer-data]
  (insert (merge { :created_at (new Date) :user_id (:id (user/current-user)) }
            (select-keys offer-data [:has_amount :has_currency :has_payment_type :wants_amount :wants_currency
                                     :wants_payment_type :identity_id :foreign_offer_id :closed]))))

(defn update-from-foreign-offer [offer-id foreign-offer]
  (update
    { :id offer-id
      :foreign_offer_id (:id foreign-offer)
      :has_amount (:wants_amount foreign-offer)
      :has_currency (:wants_currency foreign-offer)
      :has_payment_type (:wants_payment_type foreign-offer)
      :wants_amount (:has_amount foreign-offer)
      :wants_currency (:has_currency foreign-offer)
      :wants_payment_type (:has_payment_type foreign-offer) })
  (get-record offer-id))

(defn update-or-create-offer [offer-data]
  (when offer-data
    (or
      (find-record (select-keys offer-data [:user_id :identity_id :foreign_offer_id]))
      (get-record (create-new-offer offer-data)))))

(defn all-offers []
  (find-records [true]))

(defn open-offer? [offer]
  (as-boolean (not (:closed offer))))

(defn open-offers
  ([] (open-offers (user/current-user)))
  ([user] (find-records ["(closed IS NULL OR closed = 0) AND user_id = ?" (:id user)])))

(defn currency [offer currency-key]
  (currency/get-currency (currency-key offer)))

(defn amount-str [offer amount-key currency-key]
  (str (amount-key offer) " " (currency/currency-str (currency offer currency-key))))

(defn payment-type [offer payment-key]
  (payment-type/get-payment (payment-key offer)))

(defn payment-type-str [offer payment-key]
  (payment-type/payment-type-str (payment-type offer payment-key)))

(defn has-currency [offer]
  (currency offer :has_currency))

(defn has-amount-str [offer]
  (amount-str offer :has_amount :has_currency))

(defn has-payment-type [offer]
  (payment-type offer :has_payment_type))

(defn has-payment-type-str [offer]
  (payment-type-str offer :has_payment_type))

(defn wants-currency [offer]
  (currency offer :wants_currency))

(defn wants-amount-str [offer]
  (amount-str offer :wants_amount :wants_currency))

(defn wants_payment-type [offer]
  (payment-type offer :wants_payment_type))

(defn wants-payment-type-str [offer]
  (payment-type-str offer :wants_payment_type))

(defn convert-to-table-offer [offer]
  { :id (:id offer)
    :i-have-amount (has-amount-str offer)
    :i-want-to-send-by (has-payment-type-str offer)
    :i-want-amount (wants-amount-str offer)
    :i-want-to-receive-by (wants-payment-type-str offer) })

(defn table-open-offers []
  (map convert-to-table-offer (open-offers)))

(defn delete-offer [offer-id]
  (destroy-record { :id offer-id }))

(defn search-offers [search-args]
  (find-by-sql ["SELECT * FROM offers WHERE (closed IS NULL OR closed = 0) AND user_id = ? AND has_currency = ? AND has_payment_type = ? AND wants_currency = ? AND wants_payment_type = ?"
                (:id (user/current-user)) (:i-want-currency search-args) (:i-want-payment-type search-args)
                (:i-have-currency search-args) (:i-have-payment-type search-args)]))

(defn close-offer [offer]
  (when-let [local-offer (find-record { :id (:id offer) })]
    (when (open-offer? local-offer)
      (update { :id (:id offer) :closed 1 })
      (get-record (:id offer)))))

(defn reopen-offer [offer]
  (update { :id (:id offer) :closed 0 }))