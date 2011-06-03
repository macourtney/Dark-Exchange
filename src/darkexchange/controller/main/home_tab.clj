(ns darkexchange.controller.main.home-tab
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as action-utils]
            [darkexchange.controller.offer.new-offer :as new-offer]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.view.main.home.open-offer-panel :as open-offer-panel]
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
    (offer-model/delete-offer (:id (seesaw-table/value-at open-offer-table selected-row-index)))
    (seesaw-table/remove-at! open-offer-table selected-row-index)))

(defn attach-delete-offer-action [main-frame]
  (action-utils/attach-listener main-frame "#delete-open-offer-button"
    (fn [_] (delete-selected-offer main-frame))))

(defn load-data [main-frame]
  (load-open-offer-table main-frame))

(defn attach [main-frame]
  (attach-delete-offer-action (attach-add-offer-action main-frame)))

(defn init [main-frame]
  (attach (load-data main-frame)))