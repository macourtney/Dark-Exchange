(ns darkexchange.controller.main.search.search-tab
  (:require [darkexchange.controller.offer.has-panel :as offer-has-panel]
            [darkexchange.controller.offer.wants-panel :as offer-wants-panel]
            [darkexchange.model.calls.search-offers :as search-offers-call]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.view.main.search.search-tab :as search-tab-view]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table]))

(defn find-search-button [parent-component]
  (seesaw-core/select parent-component ["#search-button"]))

(defn find-search-offer-table [parent-component]
  (seesaw-core/select parent-component ["#search-offer-table"]))

(defn insert-offer-into-table [parent-component offer]
  (let [search-offer-table (find-search-offer-table parent-component)]
      (seesaw-table/insert-at! search-offer-table (seesaw-table/row-count search-offer-table) offer)))

(defn load-search-offer-table [parent-component offers]
  (when offers
    (doseq [offer offers]
      (insert-offer-into-table parent-component offer))))

(defn convert-offer [offer]
  { :name (:name offer)
    :has (offer-model/has-amount-str offer)
    :to_send_by (offer-model/has-payment-type-str offer)
    :wants (offer-model/wants-amount-str offer)
    :to_receive_by (offer-model/wants-payment-type-str offer) })

(defn search-call-back [parent-component found-offers]
  (when found-offers
    (load-search-offer-table parent-component (map convert-offer found-offers))))

(defn run-search [parent-component]
  (search-offers-call/call
    (offer-has-panel/i-have-currency parent-component)
    (offer-has-panel/i-have-payment-type parent-component)
    (offer-wants-panel/i-want-currency parent-component)
    (offer-wants-panel/i-want-payment-type parent-component)
    #(search-call-back parent-component %)))

(defn attach-search-action [parent-component]
  (seesaw-core/listen (find-search-button parent-component)
    :action (fn [e] (run-search parent-component))))

(defn attach [main-frame]
  (offer-has-panel/load-data main-frame)
  (offer-wants-panel/load-data main-frame)
  (attach-search-action main-frame))