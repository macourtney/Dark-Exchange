(ns darkexchange.view.main.home.open-trade-panel
  (:require [darkexchange.model.terms :as terms]
            [seesaw.core :as seesaw-core]))

(def open-trade-table-columns [ { :key :id :text (terms/id) }
                                { :key :im-sending-amount :text (terms/im-sending-amount) }
                                { :key :im-sending-by :text (terms/im-sending-by) }
                                { :key :im-receiving-amount :text (terms/im-receiving-amount) }
                                { :key :im-receiving-by :text (terms/im-receiving-by) }
                                { :key :waiting-for :text (terms/waiting-for) }])

(defn create-table-header-text []
  (terms/open-trades))

(defn create-table-header-buttons []
  (seesaw-core/horizontal-panel :items 
    [ (seesaw-core/button :id :view-open-trade-button :text (terms/view) :enabled? false) ]))

(defn create-table-header []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :west (create-table-header-text)
      :east (create-table-header-buttons)))

(defn create-table []
  (seesaw-core/scrollable
    (seesaw-core/table :id :open-trade-table
      :model [ :columns open-trade-table-columns ])
    :preferred-size [600 :by 300]))

(defn create []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :north (create-table-header)
      :center (create-table)))