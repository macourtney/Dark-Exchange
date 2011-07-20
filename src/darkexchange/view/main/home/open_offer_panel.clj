(ns darkexchange.view.main.home.open-offer-panel
  (:require [darkexchange.model.terms :as terms]
            [darkexchange.view.offer.open-offer-table :as open-offer-table-view]
            [seesaw.core :as seesaw-core]))

(defn create-table-header-text []
  (terms/open-offers))

(defn create-table-header-buttons []
  (seesaw-core/horizontal-panel :items 
    [ (seesaw-core/button :id :new-open-offer-button :text (terms/new))
      [:fill-h 3]
      (seesaw-core/button :id :delete-open-offer-button :text (terms/delete) :enabled? false)]))

(defn create-table-header []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :west (create-table-header-text)
      :east (create-table-header-buttons)))

(defn create []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :north (create-table-header)
      :center (open-offer-table-view/create { :id :open-offer-table })))