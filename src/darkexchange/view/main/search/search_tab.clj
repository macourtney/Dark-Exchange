(ns darkexchange.view.main.search.search-tab
  (:require [darkexchange.model.terms :as terms]
            [darkexchange.view.main.search.has-panel :as has-panel]
            [darkexchange.view.main.search.wants-panel :as wants-panel]
            [seesaw.core :as seesaw-core]))

(def tab-name (terms/search))

(def table-columns [ { :key :id :text (terms/id) }
                          { :key :destination :text (terms/destination) }
                          { :key :created_at :text (terms/created-on) }
                          { :key :updated_at :text (terms/last-updated-at) }
                          { :key :notified :text (terms/notified) }])

(defn create-criteria-panel []
  (seesaw-core/horizontal-panel
    :border 5
    :items [(seesaw-core/vertical-panel :items [(has-panel/create) [:fill-v 5] (wants-panel/create)])
            [:fill-h 5]
            (seesaw-core/button :id :search-button :text (terms/search))]))

(defn list-buttons []
  (seesaw-core/horizontal-panel :items 
    [ (seesaw-core/button :id :accept-offer-button :text (terms/accept-offer)) ]))

(defn list-header-panel []
  (seesaw-core/border-panel
    :west (terms/offers)
    :east (list-buttons)))

(defn list-table []
  (seesaw-core/scrollable
    (seesaw-core/table :id :search-offer-table :preferred-size [600 :by 300]
      :model [ :columns table-columns ])))

(defn create-list-panel []
  (seesaw-core/border-panel
    :vgap 3
    :north (list-header-panel)
    :center (list-table)))

(defn create []
  (seesaw-core/border-panel
    :border 5
    :vgap 5
    :north (create-criteria-panel)
    :center (create-list-panel)))