(ns darkexchange.controller.offer.open-offer-table
  (:require [darkexchange.controller.widgets.utils :as widgets-utils]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.view.offer.open-offer-table :as open-offer-table-view]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table]))

(defn find-open-offer-table [parent-component]
  (seesaw-core/select parent-component ["#open-offer-table"]))

(defn load-open-offer-table [parent-component open-offers]
  (when open-offers
    (seesaw-core/config! (find-open-offer-table parent-component)
      :model [:columns open-offer-table-view/table-columns
              :rows open-offers]))
  parent-component)

(defn add-offer [parent-component offer]
  (when (offer-model/open-offer? offer)
    (seesaw-table/insert-at! (find-open-offer-table parent-component) 0 (offer-model/convert-to-table-offer offer))))

(defn add-offer-id [parent-component offer-id]
  (add-offer parent-component (offer-model/get-record offer-id)))

(defn selected-offer [parent-component]
  (let [open-offer-table (find-open-offer-table parent-component)]
    (seesaw-table/value-at open-offer-table (.getSelectedRow open-offer-table))))

(defn selected-offer-id [parent-component]
  (:id (selected-offer parent-component)))

(defn find-offer-index [open-offer-table offer]
  (some #(when (= (:id offer) (:id (second %1))) (first %1))
    (map #(list %1 (seesaw-table/value-at open-offer-table %1)) (range (seesaw-table/row-count open-offer-table)))))

(defn delete-offer [parent-component offer]
  (let [open-offer-table (find-open-offer-table parent-component)]
    (when-let [offer-index (find-offer-index open-offer-table offer)]
      (seesaw-table/remove-at! open-offer-table offer-index))))

(defn replace-offer-at [open-offer-table offer offer-index]
  (seesaw-table/remove-at! open-offer-table offer-index)
  (when (offer-model/open-offer? offer)
    (seesaw-table/insert-at! open-offer-table offer-index (offer-model/convert-to-table-offer offer))))

(defn update-offer [parent-component offer]
  (let [open-offer-table (find-open-offer-table parent-component)]
    (when-let [offer-index (find-offer-index open-offer-table offer)]
      (replace-offer-at open-offer-table offer offer-index))))

(defn attach-single-select-button-enable-listener [parent-component button]
  (widgets-utils/single-select-table-button button (find-open-offer-table parent-component))
  parent-component)

(defn attach-table-action [parent-component action-fn]
  (widgets-utils/add-table-action (find-open-offer-table parent-component) action-fn)
  parent-component)