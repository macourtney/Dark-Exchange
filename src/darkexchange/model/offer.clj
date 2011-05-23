(ns darkexchange.model.offer
  (:require [darkexchange.model.has-offer :as has-offer-model]
            [darkexchange.model.wants-offer :as wants-offer-model])
  (:use darkexchange.model.base))

(clj-record.core/init-model
  (:associations
    (belongs-to identity :fk acceptor)
    (has-many wants-offers)
    (has-many has-offers)))

(defn all-offers []
  (find-records [true]))

(defn open-offers []
  (find-records ["acceptor IS NULL"]))

(defn attach-has-offers [offer]
  (assoc offer :has-offer (first (find-has-offers offer))))

(defn attach-wants-offers [offer]
  (assoc offer :wants-offer (first (find-wants-offers offer))))

(defn attach-has-and-wants-offers [offer]
  (attach-wants-offers (attach-has-offers offer)))

(defn convert-to-table-offer [offer]
  { :id (:id offer)
    :i-have-amount (:amount (:has-offer offer))
    :i-want-to-send-by (has-offer-model/currency-str (:has-offer offer))
    :i-want-amount (:amount (:wants-offer offer))
    :i-want-to-receive-by (wants-offer-model/currency-str (:wants-offer offer)) })

(defn table-open-offers []
  (map convert-to-table-offer (open-offers)))