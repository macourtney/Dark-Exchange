(ns darkexchange.controller.main.home-tab
  (:require [darkexchange.controller.offer.new-offer :as new-offer]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.view.main.home.open-offer-panel :as open-offer-panel]
            [seesaw.core :as seesaw-core]))

(defn find-open-offer-table [main-frame]
  (seesaw-core/select main-frame ["#open-offer-table"]))

(defn load-open-offer-table [main-frame]
  (when-let [open-offers (offer-model/table-open-offers)]
    (seesaw-core/config! (find-open-offer-table main-frame)
      :model [:columns open-offer-panel/open-offer-table-columns
              :rows open-offers])))

(defn find-new-open-offer-button [main-frame]
  (seesaw-core/select main-frame ["#new-open-offer-button"]))

(defn attach-add-offer-action [main-frame call-back]
  (seesaw-core/listen (find-new-open-offer-button main-frame)
    :action (fn [e] (new-offer/show call-back))))

(defn add-offer-call-back []
  )

(defn attach [main-frame]
  (load-open-offer-table main-frame)
  (attach-add-offer-action main-frame add-offer-call-back))