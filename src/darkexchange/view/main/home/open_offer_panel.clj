(ns darkexchange.view.main.home.open-offer-panel
  (:require [darkexchange.model.terms :as terms]
            [seesaw.core :as seesaw-core]))

(def open-offer-table-columns [ { :key :id :text (terms/id) }
                                { :key :i-have-amount :text (terms/i-have-amount) }
                                { :key :i-want-to-send-by :text (terms/i-want-to-send-by) }
                                { :key :i-want-amount :text (terms/i-want-amount) }
                                { :key :i-want-to-receive-by :text (terms/i-want-to-receive-by) } ])

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

(defn create-table []
  (seesaw-core/scrollable
    (seesaw-core/table :id :open-offer-table
      :model [ :columns open-offer-table-columns ])
    :preferred-size [600 :by 300]))

(defn create []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :north (create-table-header)
      :center (create-table)))