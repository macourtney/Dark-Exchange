(ns darkexchange.controller.main.home-tab
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as action-utils]
            [darkexchange.controller.offer.new-offer :as new-offer]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.model.trade :as trade-model]
            [darkexchange.model.user :as user-model]
            [darkexchange.view.main.home.open-offer-panel :as open-offer-panel]
            [darkexchange.view.main.home.open-trade-panel :as open-trade-panel]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table]))

(defn find-open-offer-table [main-frame]
  (seesaw-core/select main-frame ["#open-offer-table"]))

(defn load-open-offer-table [main-frame]
  (when-let [open-offers (offer-model/table-open-offers)]
    (seesaw-core/config! (find-open-offer-table main-frame)
      :model [:columns open-offer-panel/open-offer-table-columns
              :rows open-offers]))
  main-frame)

(defn create-add-offer-call-back [main-frame]
  (fn [offer-id]
    (let [offer (offer-model/get-record offer-id)]
      (when (offer-model/open-offer? offer)
        (seesaw-table/insert-at! (find-open-offer-table main-frame) 0 (offer-model/convert-to-table-offer offer))))))

(defn attach-add-offer-action [main-frame]
  (action-utils/attach-listener main-frame "#new-open-offer-button"
    (fn [e] (new-offer/show (create-add-offer-call-back main-frame)))))

(defn delete-selected-offer [main-frame]
  (let [open-offer-table (find-open-offer-table main-frame)
        selected-row-index (.getSelectedRow open-offer-table)]
    (offer-model/delete-offer (:id (seesaw-table/value-at open-offer-table selected-row-index)))))

(defn attach-delete-offer-action [main-frame]
  (action-utils/attach-listener main-frame "#delete-open-offer-button"
    (fn [_] (delete-selected-offer main-frame))))

(defn find-offer-index [open-offer-table offer]
  (some #(when (= (:id offer) (:id (second %1))) (first %1))
    (map #(list %1 (seesaw-table/value-at open-offer-table %1)) (range (seesaw-table/row-count open-offer-table)))))

(defn delete-offer-listener [main-frame offer]
  (let [open-offer-table (find-open-offer-table main-frame)]
    (when-let [offer-index (find-offer-index open-offer-table offer)]
      (seesaw-table/remove-at! open-offer-table offer-index))))

(defn attach-offer-delete-listener [main-frame]
  (offer-model/add-delete-offer-listener #(seesaw-core/invoke-later (delete-offer-listener main-frame %)))
  main-frame)

(defn replace-offer-at [open-offer-table offer offer-index]
  (seesaw-table/remove-at! open-offer-table offer-index)
  (when (offer-model/open-offer? offer)
    (seesaw-table/insert-at! open-offer-table offer-index (offer-model/convert-to-table-offer offer))))

(defn update-offer-listener [main-frame offer]
  (let [open-offer-table (find-open-offer-table main-frame)]
    (when-let [offer-index (find-offer-index open-offer-table offer)]
      (replace-offer-at open-offer-table offer offer-index))))

(defn attach-offer-update-listener [main-frame]
  (offer-model/add-update-offer-listener #(seesaw-core/invoke-later (update-offer-listener main-frame %)))
  main-frame)

(defn find-open-trade-table [main-frame]
  (seesaw-core/select main-frame ["#open-trade-table"]))

(defn load-open-trade-table [main-frame]
  (when-let [open-trades (trade-model/table-open-trades)]
    (seesaw-core/config! (find-open-trade-table main-frame)
      :model [:columns open-trade-panel/open-trade-table-columns
              :rows open-trades]))
  main-frame)

(defn new-trade-listener [main-frame trade]
  (when (and (= (:id (user-model/current-user)) (:user_id trade)) (trade-model/open-trade? trade))
    (seesaw-table/insert-at! (find-open-trade-table main-frame) 0 (trade-model/convert-to-table-trade trade))))

(defn attach-new-trade-listener [main-frame]
  (trade-model/add-trade-add-listener #(seesaw-core/invoke-later (new-trade-listener main-frame %)))
  main-frame)

(defn find-trade-index [open-trade-table trade]
  (some #(when (= (:id trade) (:id (second %1))) (first %1))
    (map #(list %1 (seesaw-table/value-at open-trade-table %1)) (range (seesaw-table/row-count open-trade-table)))))

(defn replace-trade-at [open-trade-table trade trade-index]
  (seesaw-table/remove-at! open-trade-table trade-index)
  (when (trade-model/open-trade? trade)
    (seesaw-table/insert-at! open-trade-table trade-index (trade-model/convert-to-table-trade trade))))

(defn trade-update-listener [main-frame trade]
  (let [open-trade-table (find-open-trade-table main-frame)]
    (when-let [trade-index (find-trade-index open-trade-table trade)]
      (replace-trade-at open-trade-table trade trade-index))))

(defn attach-trade-update-listener [main-frame]
  (trade-model/add-update-trade-listener #(seesaw-core/invoke-later (new-trade-listener main-frame %)))
  main-frame)

(defn load-data [main-frame]
  (load-open-trade-table (load-open-offer-table main-frame)))

(defn attach-offer-listeners [main-frame]
  (attach-offer-update-listener
    (attach-offer-delete-listener
      (attach-delete-offer-action
        (attach-add-offer-action main-frame)))))

(defn attach-trade-listeners [main-frame]
  (attach-new-trade-listener (attach-trade-update-listener main-frame)))

(defn attach [main-frame]
  (attach-trade-listeners (attach-offer-listeners main-frame)))

(defn init [main-frame]
  (attach (load-data main-frame)))