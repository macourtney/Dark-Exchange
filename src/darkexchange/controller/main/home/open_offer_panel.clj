(ns darkexchange.controller.main.home.open-offer-panel
  (:require [darkexchange.controller.actions.utils :as action-utils]
            [darkexchange.controller.offer.new-offer :as new-offer]
            [darkexchange.controller.widgets.utils :as widgets-utils]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.view.main.home.open-offer-panel :as open-offer-panel]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table]))

(defn find-open-offer-table [main-frame]
  (seesaw-core/select main-frame ["#open-offer-table"]))

(defn find-delete-open-offer-button [main-frame]
  (seesaw-core/select main-frame ["#delete-open-offer-button"]))

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
    (fn [e] (new-offer/show main-frame (create-add-offer-call-back main-frame)))))

(comment
(defn delete-selected-offer [main-frame]
  (let [open-offer-table (find-open-offer-table main-frame)
        selected-row-index (.getSelectedRow open-offer-table)]
    (offer-model/delete-offer (:id (seesaw-table/value-at open-offer-table selected-row-index)))))
)

(defn delete-selected-offer-setup [e main-frame]
  (let [open-offer-table (find-open-offer-table main-frame)]
    (:id (seesaw-table/value-at open-offer-table (.getSelectedRow open-offer-table)))))

(defn delete-selected-offer [selected-offer-id]
  (offer-model/delete-offer selected-offer-id))

(defn attach-delete-offer-action [main-frame]
  (action-utils/attach-background-listener main-frame "#delete-open-offer-button"
    { :other-params main-frame
      :before-background delete-selected-offer-setup
      :background delete-selected-offer }
    ;(fn [_] (delete-selected-offer main-frame))
    ))

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

(defn delete-open-offer-if-enabled [main-frame]
  (widgets-utils/do-click-if-enabled (find-delete-open-offer-button main-frame)))

(defn attach-delete-open-offer-enable-listener [main-frame]
  (widgets-utils/single-select-table-button (find-delete-open-offer-button main-frame)
    (find-open-offer-table main-frame))
  main-frame)

(defn attach-offer-listeners [main-frame]
  (attach-delete-open-offer-enable-listener
    (attach-offer-update-listener
      (attach-offer-delete-listener
        (attach-delete-offer-action
          (attach-add-offer-action main-frame))))))

(defn load-data [main-frame]
  (load-open-offer-table main-frame))

(defn attach [main-frame]
  (attach-offer-listeners main-frame))