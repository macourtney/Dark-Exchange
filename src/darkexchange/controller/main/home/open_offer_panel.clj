(ns darkexchange.controller.main.home.open-offer-panel
  (:require [darkexchange.controller.actions.utils :as action-utils]
            [darkexchange.controller.offer.new-offer :as new-offer]
            [darkexchange.controller.offer.open-offer-table :as open-offer-table-controller]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.controller.widgets.utils :as widgets-utils]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.view.main.home.open-offer-panel :as open-offer-panel]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table]))

(defn find-delete-open-offer-button [main-frame]
  (seesaw-core/select main-frame ["#delete-open-offer-button"]))

(defn load-open-offer-table [main-frame]
  (open-offer-table-controller/load-open-offer-table main-frame (offer-model/table-open-offers)))

(defn create-add-offer-call-back [main-frame]
  #(open-offer-table-controller/add-offer-id main-frame %))

(defn attach-add-offer-action [main-frame]
  (action-utils/attach-listener main-frame "#new-open-offer-button"
    (fn [e] (new-offer/show main-frame (create-add-offer-call-back main-frame)))))

(defn delete-selected-offer [main-frame selected-offer-id]
  (future
    (offer-model/delete-offer selected-offer-id)
    (seesaw-core/invoke-later
      (controller-utils/enable-widget main-frame))))

(defn delete-selected-offer-action [main-frame e]
  (controller-utils/disable-widget main-frame)
  (delete-selected-offer main-frame (open-offer-table-controller/selected-offer-id main-frame)))

(defn attach-delete-offer-action [main-frame]
  (action-utils/attach-frame-listener main-frame "#delete-open-offer-button" delete-selected-offer-action))

(defn attach-offer-delete-listener [main-frame]
  (offer-model/add-delete-offer-listener
    #(seesaw-core/invoke-later (open-offer-table-controller/delete-offer main-frame %)))
  main-frame)

(defn attach-offer-update-listener [main-frame]
  (offer-model/add-update-offer-listener
    #(seesaw-core/invoke-later (open-offer-table-controller/update-offer main-frame %)))
  main-frame)

(defn attach-delete-open-offer-enable-listener [main-frame]
  (widgets-utils/single-select-table-button (find-delete-open-offer-button main-frame)
    (open-offer-table-controller/find-open-offer-table main-frame))
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