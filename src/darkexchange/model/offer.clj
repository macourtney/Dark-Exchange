(ns darkexchange.model.offer
  (:require [darkexchange.model.has-offer :as has-offer-model]
            [darkexchange.model.wants-offer :as wants-offer-model])
  (:use darkexchange.model.base))

(def offer-add-listeners (atom []))

(defn add-offer-add-listener [offer-add-listener]
  (swap! offer-add-listeners conj offer-add-listener))

(defn offer-add [new-offer]
  (doseq [listener @offer-add-listeners]
    (listener new-offer)))

(clj-record.core/init-model
  (:associations
    ;(belongs-to identity :fk acceptor)
    (has-many wants-offers)
    (has-many has-offers))
  (:callbacks (:after-insert offer-add)))

(defn all-offers []
  (find-records [true]))

(defn open-offer? [offer]
  (nil? (:acceptor-id offer)))

(defn open-offers []
  (find-records ["acceptor_id IS NULL"]))

(defn attach-has-offers [offer]
  (assoc offer :has-offer (first (find-has-offers offer))))

(defn attach-wants-offers [offer]
  (assoc offer :wants-offer (first (find-wants-offers offer))))

(defn attach-has-and-wants-offers [offer]
  (attach-wants-offers (attach-has-offers offer)))

(defn convert-to-table-offer [offer]
  (let [offer (attach-has-and-wants-offers offer)]
    { :id (:id offer)
      :i-have-amount (has-offer-model/amount-str (:has-offer offer))
      :i-want-to-send-by (has-offer-model/currency-str (:has-offer offer))
      :i-want-amount (wants-offer-model/amount-str (:wants-offer offer))
      :i-want-to-receive-by (wants-offer-model/currency-str (:wants-offer offer)) }))

(defn table-open-offers []
  (map convert-to-table-offer (open-offers)))