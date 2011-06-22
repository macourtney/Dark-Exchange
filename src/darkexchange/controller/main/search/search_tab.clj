(ns darkexchange.controller.main.search.search-tab
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as action-utils]
            [darkexchange.controller.offer.has-panel :as offer-has-panel]
            [darkexchange.controller.offer.wants-panel :as offer-wants-panel]
            [darkexchange.controller.offer.view :as offer-view-controller]
            [darkexchange.controller.widgets.utils :as widgets-utils]
            [darkexchange.model.calls.search-offers :as search-offers-call]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.view.main.search.search-tab :as search-tab-view]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table]))

(defn find-search-offer-table [parent-component]
  (seesaw-core/select parent-component ["#search-offer-table"]))

(defn find-view-offer-button [parent-component]
  (seesaw-core/select parent-component ["#view-offer-button"]))

(defn insert-offer-into-table [parent-component offer]
  (let [search-offer-table (find-search-offer-table parent-component)]
    (seesaw-table/insert-at! search-offer-table (seesaw-table/row-count search-offer-table) offer)))

(defn load-search-offer-table [parent-component offers]
  (when offers
    (doseq [offer offers]
      (seesaw.core/invoke-later (insert-offer-into-table parent-component offer)))))

(defn convert-offer [offer]
  { :id (:id offer)
    :public-key (:public-key offer)
    :public-key-algorithm (:public-key-algorithm offer)
    :name (:name offer)
    :has (offer-model/has-amount-str offer)
    :to_send_by (offer-model/has-payment-type-str offer)
    :wants (offer-model/wants-amount-str offer)
    :to_receive_by (offer-model/wants-payment-type-str offer) })

(defn search-call-back [parent-component found-offers]
  (when found-offers
    (let [converted-offers (map convert-offer found-offers)]
      (load-search-offer-table parent-component converted-offers))))

(defn run-search [parent-component]
  (search-offers-call/call
    (offer-has-panel/i-have-currency parent-component)
    (offer-has-panel/i-have-payment-type parent-component)
    (offer-wants-panel/i-want-currency parent-component)
    (offer-wants-panel/i-want-payment-type parent-component)
    #(search-call-back parent-component %)))

(defn attach-search-action [parent-component]
  (action-utils/attach-listener parent-component "#search-button"
    (fn [e] (run-search parent-component))))

(defn view-offer-listener [parent-component]
  (let [search-offer-table (find-search-offer-table parent-component)
        selected-row (seesaw-table/value-at search-offer-table (seesaw-core/selection search-offer-table))]
    (offer-view-controller/show parent-component selected-row)))

(defn attach-view-offer-action [parent-component]
  (action-utils/attach-listener parent-component "#view-offer-button"
    (fn [e] (view-offer-listener parent-component))))

(defn view-offer-if-enabled [main-frame]
  (widgets-utils/do-click-if-enabled (find-view-offer-button main-frame)))

(defn attach-view-offer-table-action [main-frame]
  (widgets-utils/add-table-action (find-search-offer-table main-frame)
    #(view-offer-if-enabled main-frame))
  main-frame)

(defn attach-view-offer-enable-listener [main-frame]
  (widgets-utils/single-select-table-button (find-view-offer-button main-frame)
    (find-search-offer-table main-frame))
  main-frame)

(defn load-data [main-frame]
  (offer-wants-panel/load-data (offer-has-panel/load-data main-frame)))

(defn attach [main-frame]
  (attach-search-action main-frame)
  (attach-view-offer-action main-frame)
  (offer-has-panel/attach main-frame)
  (offer-wants-panel/attach main-frame)
  (attach-view-offer-table-action main-frame)
  (attach-view-offer-enable-listener main-frame))

(defn init [main-frame]
  (attach (load-data main-frame)))