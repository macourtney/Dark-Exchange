(ns darkexchange.model.offer
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.contrib.logging :as logging]
            [darkexchange.model.has-offer :as has-offer]
            [darkexchange.model.wants-offer :as wants-offer]
            [darkexchange.model.user :as user])
  (:use darkexchange.model.base)
  (:import [java.util Date]))

(def offer-add-listeners (atom []))

(defn add-offer-add-listener [offer-add-listener]
  (swap! offer-add-listeners conj offer-add-listener))

(defn offer-add [new-offer]
  (doseq [listener @offer-add-listeners]
    (listener new-offer)))

(defn cleanup [deleted-offer]
  (let [offer-id (:id deleted-offer)]
    (has-offer/delete-has-offers-for offer-id)
    (wants-offer/delete-wants-offers-for offer-id)))

(clj-record.core/init-model
  (:associations
    (belongs-to identity)
    (belongs-to user)
    (has-many wants-offers)
    (has-many has-offers))
  (:callbacks (:after-insert offer-add)
              (:after-destroy cleanup)))

(defn create-new-offer []
  (insert { :created_at (new Date) :user_id (:id (user/current-user)) }))

(defn all-offers []
  (find-records [true]))

(defn open-offer? [offer]
  (nil? (:identity_id offer)))

(defn open-offers
  ([] (open-offers (user/current-user)))
  ([user] (find-records ["identity_id IS NULL AND user_id = ?" (:id user)])))

(defn attach-has-offers [offer]
  (assoc offer :has-offer (first (find-has-offers offer))))

(defn attach-wants-offers [offer]
  (assoc offer :wants-offer (first (find-wants-offers offer))))

(defn attach-has-and-wants-offers [offer]
  (attach-wants-offers (attach-has-offers offer)))

(defn convert-to-table-offer [offer]
  (let [offer (attach-has-and-wants-offers offer)]
    { :id (:id offer)
      :i-have-amount (has-offer/amount-str (:has-offer offer))
      :i-want-to-send-by (has-offer/currency-str (:has-offer offer))
      :i-want-amount (wants-offer/amount-str (:wants-offer offer))
      :i-want-to-receive-by (wants-offer/currency-str (:wants-offer offer)) }))

(defn table-open-offers []
  (map convert-to-table-offer (open-offers)))

(defn delete-offer [offer-id]
  (destroy-record { :id offer-id }))

(defn search-offers [search-args]
  (find-by-sql ["SELECT offers.id FROM offers AS offers LEFT JOIN has_offers AS has_offers ON has_offers.offer_id = offers.id LEFT JOIN wants_offers AS wants_offers ON wants_offers.offer_id = offers.id WHERE has_offers.currency = ? AND has_offers.payment_type = ? AND wants_offers.currency = ? AND wants_offers.payment_type = ?"
                (:i-want-currency search-args) (:i-want-payment-type search-args) (:i-have-currency search-args)
                (:i-have-payment-type search-args)]))