(ns darkexchange.model.offer
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.contrib.logging :as logging]
            [darkexchange.model.currency :as currency]
            [darkexchange.model.payment-type :as payment-type]
            [darkexchange.model.user :as user])
  (:use darkexchange.model.base)
  (:import [java.util Date]))

(def offer-add-listeners (atom []))

(defn add-offer-add-listener [offer-add-listener]
  (swap! offer-add-listeners conj offer-add-listener))

(defn offer-add [new-offer]
  (doseq [listener @offer-add-listeners]
    (listener new-offer)))

(clj-record.core/init-model
  (:associations
    (belongs-to identity)
    (belongs-to user)
    (has-many wants-offers)
    (has-many has-offers))
  (:callbacks (:after-insert offer-add)))

(defn create-new-offer [offer-data]
  (insert (merge { :created_at (new Date) :user_id (:id (user/current-user)) }
            (select-keys offer-data [:has_amount :has_currency :has_payment_type :wants_amount :wants_currency
                                     :wants_payment_type]))))

(defn all-offers []
  (find-records [true]))

(defn open-offer? [offer]
  (nil? (:identity_id offer)))

(defn open-offers
  ([] (open-offers (user/current-user)))
  ([user] (find-records ["identity_id IS NULL AND user_id = ?" (:id user)])))

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
  (amount-str offer :want_amount :wants_currency))

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
  (find-by-sql ["SELECT * FROM offers WHERE identity_id IS NULL AND user_id = ? AND has_currency = ? AND has_payment_type = ? AND wants_currency = ? AND wants_payment_type = ?"
                (:id (user/current-user)) (:i-want-currency search-args) (:i-want-payment-type search-args)
                (:i-have-currency search-args) (:i-have-payment-type search-args)]))